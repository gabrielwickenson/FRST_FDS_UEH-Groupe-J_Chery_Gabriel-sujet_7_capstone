package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.AvisRequest;
import com.capstone.serviceplatform.dto.LitigeRequest;
import com.capstone.serviceplatform.dto.ReservationRequest;
import com.capstone.serviceplatform.entity.*;
import com.capstone.serviceplatform.repository.*;
import com.capstone.serviceplatform.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PrestataireRepository prestataireRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private AvisRepository avisRepository;
    @Autowired
    private LitigeRepository litigeRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private DisponibiliteRepository disponibiliteRepository;
    @Autowired
    private UserRepository userRepository;

    // -------------------- CRÉATION DE RÉSERVATION --------------------
    @PostMapping
    public ResponseEntity<?> creerReservation(@RequestBody @Valid ReservationRequest request) {
        Client client = clientRepository.findById(request.getClientId()).orElse(null);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Client non trouvé"));
        }

        Prestataire prestataire = prestataireRepository.findById(request.getPrestataireId()).orElse(null);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        Service service = serviceRepository.findById(request.getServiceId()).orElse(null);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Service non trouvé"));
        }

        // Vérification compétence
        String competences = prestataire.getCompetences() != null ? prestataire.getCompetences().toLowerCase() : "";
        String serviceNom = service.getNom().toLowerCase();
        if (!competences.contains(serviceNom)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ce prestataire ne propose pas le service demandé : " + service.getNom()));
        }

        // Vérification conflit de créneau
        boolean conflit = reservationRepository.existsConflit(prestataire.getId(), request.getDateHeure());
        if (conflit) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Le prestataire est déjà réservé à cette date/heure"));
        }

        // Vérification disponibilités
        String jourDemande = request.getDateHeure().getDayOfWeek().name();
        LocalTime heureDemande = request.getDateHeure().toLocalTime();
        List<Disponibilite> dispoList = disponibiliteRepository.findByPrestataire(prestataire);
        boolean dispoOk = dispoList.stream().anyMatch(d ->
                d.getJour().equalsIgnoreCase(jourDemande) &&
                        !heureDemande.isBefore(d.getHeureDebut()) &&
                        !heureDemande.isAfter(d.getHeureFin())
        );
        if (!dispoOk) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Le prestataire n'est pas disponible à cette date/heure"));
        }

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setPrestataire(prestataire);
        reservation.setService(service);
        reservation.setDateHeure(request.getDateHeure());
        reservation.setAdresse(request.getAdresse());
        reservation.setStatut("EN_ATTENTE");
        reservation.setMontant(BigDecimal.valueOf(request.getMontant()));

        Reservation saved = reservationRepository.save(reservation);

        // Notification au prestataire
        if (prestataire.getFcmToken() != null && !prestataire.getFcmToken().isEmpty()) {
            try {
                notificationService.envoyerNotification(
                        prestataire.getFcmToken(),
                        prestataire,
                        "Nouvelle réservation",
                        "Nouvelle demande de " + client.getNom() + " le " + request.getDateHeure()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        saved.setClient(null);
        saved.setPrestataire(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // -------------------- MISE À JOUR STATUT --------------------
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Long id,
                                          @RequestParam String statut,
                                          @RequestParam Long prestataireId) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }
        if (!reservation.getPrestataire().getId().equals(prestataireId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier cette réservation"));
        }
        List<String> statutsValides = Arrays.asList("EN_ATTENTE", "ACCEPTEE", "REFUSEE", "EN_COURS", "TERMINEE", "ANNULEE");
        if (!statutsValides.contains(statut)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Statut invalide"));
        }
        reservation.setStatut(statut);
        Reservation updated = reservationRepository.save(reservation);

        // Notification au client
        String clientToken = reservation.getClient().getFcmToken();
        if (clientToken != null && !clientToken.isEmpty()) {
            String message;
            switch (statut) {
                case "ACCEPTEE":
                    message = "Votre réservation a été acceptée par le prestataire.";
                    break;
                case "REFUSEE":
                    message = "Votre réservation a été refusée.";
                    break;
                case "EN_COURS":
                    message = "Le prestataire est en route.";
                    break;
                case "TERMINEE":
                    message = "La prestation est terminée. Merci de votre confiance.";
                    break;
                case "ANNULEE":
                    message = "Votre réservation a été annulée.";
                    break;
                default:
                    message = "Le statut de votre réservation a changé : " + statut;
            }
            try {
                notificationService.envoyerNotification(clientToken, reservation.getClient(),
                        "Mise à jour de réservation", message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updated.setClient(null);
        updated.setPrestataire(null);
        return ResponseEntity.ok(updated);
    }

    // -------------------- CONSULTATION RÉSERVATIONS (CLIENT) --------------------
    // Ancien endpoint avec ID – protégé par vérification de propriétaire
    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getReservationsByClient(@PathVariable Long clientId) {
        // Récupérer l'utilisateur authentifié
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Non authentifié"));
        }
        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non trouvé"));
        }
        // Vérifier que le client demandé est bien celui connecté
        if (!currentUser.getId().equals(clientId) || currentUser.getRole() != Role.CLIENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès non autorisé à ces réservations"));
        }

        List<Reservation> reservations = reservationRepository.findByClientId(clientId);
        reservations.forEach(r -> {
            r.setClient(null);
            if (r.getPrestataire() != null) {
                r.getPrestataire().setMotDePasse(null);
            }
        });
        return ResponseEntity.ok(reservations);
    }

    // Nouvel endpoint sécurisé : réservations du client connecté
    @GetMapping("/me/client")
    public ResponseEntity<?> getMesReservationsClient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non trouvé"));
        }
        if (currentUser.getRole() != Role.CLIENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux clients"));
        }

        List<Reservation> reservations = reservationRepository.findByClientId(currentUser.getId());
        reservations.forEach(r -> {
            r.setClient(null);
            if (r.getPrestataire() != null) {
                r.getPrestataire().setMotDePasse(null);
            }
        });
        return ResponseEntity.ok(reservations);
    }

    // -------------------- CONSULTATION RÉSERVATIONS (PRESTATAIRE) --------------------
    // Ancien endpoint avec ID – protégé par vérification de propriétaire
    @GetMapping("/prestataire/{prestataireId}")
    public ResponseEntity<?> getReservationsByPrestataire(@PathVariable Long prestataireId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Non authentifié"));
        }
        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non trouvé"));
        }
        if (!currentUser.getId().equals(prestataireId) || currentUser.getRole() != Role.PRESTATAIRE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès non autorisé à ces réservations"));
        }

        List<Reservation> reservations = reservationRepository.findByPrestataireId(prestataireId);
        reservations.forEach(r -> {
            r.setPrestataire(null);
            if (r.getClient() != null) {
                r.getClient().setMotDePasse(null);
            }
        });
        return ResponseEntity.ok(reservations);
    }

    // Nouvel endpoint sécurisé : réservations du prestataire connecté
    @GetMapping("/me/prestataire")
    public ResponseEntity<?> getMesReservationsPrestataire() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non trouvé"));
        }
        if (currentUser == null || currentUser.getRole() != Role.PRESTATAIRE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux prestataires"));
        }

        List<Reservation> reservations = reservationRepository.findByPrestataireId(currentUser.getId());
        reservations.forEach(r -> {
            r.setPrestataire(null);
            if (r.getClient() != null) {
                r.getClient().setMotDePasse(null);
            }
        });
        return ResponseEntity.ok(reservations);
    }

    // -------------------- ÉVALUATION (CLIENT) --------------------
    @PostMapping("/{id}/avis")
    public ResponseEntity<?> laisserAvis(@PathVariable Long id,
                                         @RequestBody @Valid AvisRequest avisRequest,
                                         @RequestParam Long clientId) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }
        if (!reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à évaluer cette réservation"));
        }
        if (!"TERMINEE".equals(reservation.getStatut())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Seules les prestations terminées peuvent être évaluées"));
        }

        Avis avis = new Avis();
        avis.setReservation(reservation);
        avis.setNote(avisRequest.getNote());
        avis.setCommentaire(avisRequest.getCommentaire());
        avis.setDate(new Date());
        avisRepository.save(avis);

        // Mise à jour moyenne
        Prestataire prestataire = reservation.getPrestataire();
        List<Avis> avisList = avisRepository.findByReservationPrestataireId(prestataire.getId());
        double moyenne = avisList.stream().mapToInt(Avis::getNote).average().orElse(0.0);
        prestataire.setMoyenneNotes(BigDecimal.valueOf(moyenne));
        prestataireRepository.save(prestataire);

        return ResponseEntity.status(HttpStatus.CREATED).body(avis);
    }

    // -------------------- LITIGES --------------------
    @PostMapping("/{id}/litige")
    public ResponseEntity<?> ouvrirLitige(@PathVariable Long id,
                                          @RequestBody @Valid LitigeRequest request,
                                          @RequestParam Long clientId) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null)
            return ResponseEntity.notFound().build();
        if (!reservation.getClient().getId().equals(clientId))
            return ResponseEntity.status(403).body(Map.of("error", "Non autorisé"));
        if (litigeRepository.existsByReservationId(id))
            return ResponseEntity.status(409).body(Map.of("error", "Litige déjà existant"));

        Litige litige = new Litige();
        litige.setReservation(reservation);
        litige.setMotif(request.getMotif());
        litige.setStatut("OUVERT");
        litigeRepository.save(litige);
        return ResponseEntity.status(201).body(litige);
    }

    // Admin : consulter tous les litiges ouverts
    @GetMapping("/litiges/ouverts")
    public ResponseEntity<List<Litige>> getLitigesOuverts() {
        return ResponseEntity.ok(litigeRepository.findByStatut("OUVERT"));
    }

    // Admin : résoudre un litige
    @PutMapping("/litiges/{litigeId}")
    public ResponseEntity<?> resoudreLitige(@PathVariable Long litigeId,
                                            @RequestParam String resolution) {
        Litige litige = litigeRepository.findById(litigeId).orElse(null);
        if (litige == null) return ResponseEntity.notFound().build();
        litige.setStatut("RESOLU");
        litige.setResolution(resolution);
        litigeRepository.save(litige);
        return ResponseEntity.ok(litige);
    }

    // -------------------- PAIEMENT SIMULÉ --------------------
    @PostMapping("/{id}/paiement")
    public ResponseEntity<?> simulerPaiement(@PathVariable Long id,
                                             @RequestParam String modePaiement,
                                             @RequestParam Long clientId) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }
        if (!reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Non autorisé"));
        }
        reservation.setStatut("PAYEE");
        reservationRepository.save(reservation);
        return ResponseEntity.ok(Map.of("message", "Paiement simulé effectué avec " + modePaiement));
    }
}