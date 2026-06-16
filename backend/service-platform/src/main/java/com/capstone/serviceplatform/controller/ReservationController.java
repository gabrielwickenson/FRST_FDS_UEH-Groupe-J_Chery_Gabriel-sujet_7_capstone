package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.AvisRequest;
import com.capstone.serviceplatform.dto.LitigeRequest;
import com.capstone.serviceplatform.service.NotificationService;
import com.capstone.serviceplatform.dto.ReservationRequest;
import com.capstone.serviceplatform.entity.*;
import com.capstone.serviceplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<?> creerReservation(@RequestBody ReservationRequest request) {
        // Vérifier l'existence du client
        Client client = clientRepository.findById(request.getClientId())
                .orElse(null);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Client non trouvé"));
        }

        // Vérifier l'existence du prestataire
        Prestataire prestataire = prestataireRepository.findById(request.getPrestataireId())
                .orElse(null);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // Vérifier l'existence du service
        Service service = serviceRepository.findById(request.getServiceId())
                .orElse(null);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Service non trouvé"));
        }

        // Vérifier que le prestataire possède la compétence correspondant au service
        String competences = prestataire.getCompetences() != null ? prestataire.getCompetences().toLowerCase() : "";
        String serviceNom = service.getNom().toLowerCase();
        if (!competences.contains(serviceNom)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ce prestataire ne propose pas le service demandé : " + service.getNom()));
        }

        // Vérifier si le créneau est déjà pris pour ce prestataire (conflit)
        boolean conflit = reservationRepository.existsConflit(prestataire.getId(), request.getDateHeure());
        if (conflit) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Le prestataire est déjà réservé à cette date/heure"));
        }

        // Vérification des disponibilités (jour et heure)
        String jourDemande = request.getDateHeure().getDayOfWeek().name(); // ex: "MONDAY"
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

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setPrestataire(prestataire);
        reservation.setService(service);
        reservation.setDateHeure(request.getDateHeure());
        reservation.setAdresse(request.getAdresse());
        reservation.setStatut("EN_ATTENTE");
        reservation.setMontant(BigDecimal.valueOf(request.getMontant()));

        Reservation saved = reservationRepository.save(reservation);
        // Envoi d'une notification push au prestataire
        if (prestataire.getFcmToken() != null && !prestataire.getFcmToken().isEmpty()) {
            try {
                notificationService.envoyerNotification(
                        prestataire.getFcmToken(),prestataire,
                        "Nouvelle réservation",
                        "Nouvelle demande de " + client.getNom() + " le " + request.getDateHeure()
                );
            } catch (Exception e) {
                e.printStackTrace(); // Ne pas bloquer le flux principal
            }
        }
        saved.setClient(null); // éviter les cycles JSON (optionnel)
        saved.setPrestataire(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Long id,
                                          @RequestParam String statut,
                                          @RequestParam Long prestataireId) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }
        // Vérifier que le prestataire est bien celui associé à la réservation
        if (!reservation.getPrestataire().getId().equals(prestataireId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier cette réservation"));
        }
        // Liste des statuts autorisés (optionnel)
        List<String> statutsValides = Arrays.asList("EN_ATTENTE", "ACCEPTEE", "REFUSEE", "EN_COURS", "TERMINEE", "ANNULEE");
        if (!statutsValides.contains(statut)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Statut invalide"));
        }
        reservation.setStatut(statut);
        Reservation updated = reservationRepository.save(reservation);
        // Envoi d'une notification au client (si son token existe)
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
                notificationService.envoyerNotification(clientToken, reservation.getClient(),"Mise à jour de réservation", message);
            } catch (Exception e) {
                e.printStackTrace(); // Ne pas bloquer la mise à jour du statut
            }
        }
        updated.setClient(null);
        updated.setPrestataire(null);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Reservation>> getReservationsByClient(@PathVariable Long clientId) {
        List<Reservation> reservations = reservationRepository.findByClientId(clientId);
        reservations.forEach(r -> {
            r.setClient(null);
            r.getPrestataire().setMotDePasse(null);
        });
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/prestataire/{prestataireId}")
    public ResponseEntity<List<Reservation>> getReservationsByPrestataire(@PathVariable Long prestataireId) {
        List<Reservation> reservations = reservationRepository.findByPrestataireId(prestataireId);
        reservations.forEach(r -> {
            r.setPrestataire(null);
            r.getClient().setMotDePasse(null);
        });
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{id}/avis")
    public ResponseEntity<?> laisserAvis(@PathVariable Long id,
                                         @RequestBody AvisRequest avisRequest,
                                         @RequestParam Long clientId) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }
        // Vérifier que le client est bien celui de la réservation
        if (!reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à évaluer cette réservation"));
        }
        // Vérifier que la réservation est terminée
        if (!"TERMINEE".equals(reservation.getStatut())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Seules les prestations terminées peuvent être évaluées"));
        }
        // Vérifier si un avis existe déjà (optionnel)
        // Créer l'avis
        Avis avis = new Avis();
        avis.setReservation(reservation);
        avis.setNote(avisRequest.getNote());
        avis.setCommentaire(avisRequest.getCommentaire());
        avis.setDate(new Date());
        avisRepository.save(avis);
        // Mettre à jour la moyenne du prestataire
        Prestataire prestataire = reservation.getPrestataire();
        List<Avis> avisList = avisRepository.findByReservationPrestataireId(prestataire.getId());
        double moyenne = avisList.stream().mapToInt(Avis::getNote).average().orElse(0.0);
        prestataire.setMoyenneNotes(BigDecimal.valueOf(moyenne));
        prestataireRepository.save(prestataire);
        return ResponseEntity.status(HttpStatus.CREATED).body(avis);
    }

    // Ouvrir un litige (client)
    @PostMapping("/{id}/litige")
    public ResponseEntity<?> ouvrirLitige(@PathVariable Long id,
                                          @RequestBody LitigeRequest request,
                                          @RequestParam Long clientId) {
        //Vérifie si la réservation existe.
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null)
            return ResponseEntity.notFound().build();
        //Vérifie que le client qui ouvre le litige est bien celui associé à la réservation.
        if (!reservation.getClient().getId().equals(clientId))
            return ResponseEntity.status(403).body(Map.of("error", "Non autorisé"));
        //Vérifie si un litige existe déjà pour cette réservation.
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

    @PostMapping("/{id}/paiement")
    public ResponseEntity<?> simulerPaiement(@PathVariable Long id,
                                             @RequestParam String modePaiement,
                                             @RequestParam Long clientId) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }
        // Vérifier que le client est bien celui de la réservation
        if (!reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Non autorisé"));
        }
        // Vérifier que la réservation n'est pas déjà payée (si vous avez un champ paiement)
        // Ici on change simplement le statut
        reservation.setStatut("PAYEE");
        reservationRepository.save(reservation);

        // Optionnel : créer un enregistrement dans une table Paiement
        return ResponseEntity.ok(Map.of("message", "Paiement simulé effectué avec " + modePaiement));
    }
}