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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Réservations", description = "Gestion des réservations, avis, litiges et paiement")
@SecurityRequirement(name = "Bearer Authentication") // toutes les méthodes de ce contrôleur nécessitent un token
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
    @Operation(summary = "Créer une nouvelle réservation (client)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réservation créée"),
            @ApiResponse(responseCode = "404", description = "Client, prestataire ou service non trouvé"),
            @ApiResponse(responseCode = "409", description = "Conflit de créneau"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
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
    @Operation(summary = "Mettre à jour le statut d'une réservation (prestataire)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "400", description = "Statut invalide"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<?> updateStatut(@Parameter(description = "ID de la réservation") @PathVariable Long id,
                                          @Parameter(description = "Nouveau statut (EN_ATTENTE, ACCEPTEE, REFUSEE, EN_COURS, TERMINEE, ANNULEE)") @RequestParam String statut,
                                          @Parameter(description = "ID du prestataire pour vérification") @RequestParam Long prestataireId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un PRESTATAIRE
        if (currentUser.getRole() != Role.PRESTATAIRE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux prestataires"));
        }

        // 3. Vérifier que l'ID du prestataire fourni correspond à l'ID de l'utilisateur connecté
        if (!currentUser.getId().equals(prestataireId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Le prestataireId fourni ne correspond pas à votre compte"));
        }

        // 4. Récupérer la réservation
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }

        // 5. Vérifier que le prestataire associé à la réservation correspond bien au prestataire connecté
        if (!reservation.getPrestataire().getId().equals(prestataireId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier cette réservation"));
        }

        // 6. Valider le statut
        List<String> statutsValides = Arrays.asList("EN_ATTENTE", "ACCEPTEE", "REFUSEE", "EN_COURS", "TERMINEE", "ANNULEE");
        if (!statutsValides.contains(statut)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Statut invalide"));
        }

        // 7. Mettre à jour la réservation
        reservation.setStatut(statut);
        Reservation updated = reservationRepository.save(reservation);

        // 8. Notification au client
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
    @Operation(summary = "Historique des réservations d'un client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réservations"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (rôle incorrect ou client non autorisé)")
    })
    public ResponseEntity<?> getReservationsByClient(@PathVariable Long clientId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un CLIENT
        if (currentUser.getRole() != Role.CLIENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux clients"));
        }

        // 3. Vérifier que l'ID dans l'URL correspond à l'ID du client authentifié
        if (!currentUser.getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à consulter les réservations d'un autre client"));
        }

        // 4. Récupérer les réservations
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
    @GetMapping("/prestataire/{prestataireId}")
    @Operation(summary = "Récupérer les réservations d'un prestataire (agenda)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réservations"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (rôle incorrect ou prestataire non autorisé)")
    })
    public ResponseEntity<?> getReservationsByPrestataire(@PathVariable Long prestataireId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un PRESTATAIRE
        if (currentUser.getRole() != Role.PRESTATAIRE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux prestataires"));
        }

        // 3. Vérifier que l'ID dans l'URL correspond à l'ID du prestataire authentifié
        if (!currentUser.getId().equals(prestataireId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à consulter les réservations d'un autre prestataire"));
        }

        // 4. Récupérer les réservations
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
    @Operation(summary = "Agenda du prestataire connecté (ses réservations)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réservations"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux prestataires")
    })
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
    @Operation(summary = "Laisser un avis sur une réservation (client)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avis créé"),
            @ApiResponse(responseCode = "400", description = "Réservation non terminée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (rôle incorrect ou client non autorisé)"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<?> laisserAvis(@PathVariable Long id,
                                         @RequestBody @Valid AvisRequest avisRequest,
                                         @RequestParam Long clientId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un CLIENT
        if (currentUser.getRole() != Role.CLIENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux clients"));
        }

        // 3. Vérifier que l'ID du client fourni correspond à l'utilisateur connecté
        if (!currentUser.getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à laisser un avis pour un autre client"));
        }

        // 4. Récupérer la réservation
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }

        // 5. Vérifier que le client de la réservation correspond bien au client connecté (redondance)
        if (!reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à évaluer cette réservation"));
        }

        // 6. Vérifier que la réservation est terminée
        if (!"TERMINEE".equals(reservation.getStatut())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Seules les prestations terminées peuvent être évaluées"));
        }

        // 7. Créer et sauvegarder l'avis
        Avis avis = new Avis();
        avis.setReservation(reservation);
        avis.setNote(avisRequest.getNote());
        avis.setCommentaire(avisRequest.getCommentaire());
        avis.setDate(new Date());
        avisRepository.save(avis);

        // 8. Mettre à jour la moyenne du prestataire
        Prestataire prestataire = reservation.getPrestataire();
        List<Avis> avisList = avisRepository.findByReservationPrestataireId(prestataire.getId());
        double moyenne = avisList.stream().mapToInt(Avis::getNote).average().orElse(0.0);
        prestataire.setMoyenneNotes(BigDecimal.valueOf(moyenne));
        prestataireRepository.save(prestataire);

        return ResponseEntity.status(HttpStatus.CREATED).body(avis);
    }

    @GetMapping("/{id}/avis")
    @Operation(summary = "Récupérer tous les avis d'un prestataire")
    public ResponseEntity<List<Avis>> getAvisByPrestataire(@PathVariable Long id) {
        // Vérifier que le prestataire existe
        if (!prestataireRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<Avis> avis = avisRepository.findByReservationPrestataireId(id);
        return ResponseEntity.ok(avis);
    }
    
    // -------------------- LITIGES --------------------
    @PostMapping("/{id}/litige")
    @Operation(summary = "Ouvrir un litige (client)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Litige ouvert"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (rôle incorrect ou client non autorisé)"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée"),
            @ApiResponse(responseCode = "409", description = "Litige déjà existant")
    })
    public ResponseEntity<?> ouvrirLitige(@Parameter(description = "ID de la réservation") @PathVariable Long id,
                                          @Valid @RequestBody LitigeRequest request,
                                          @Parameter(description = "ID du client pour vérification") @RequestParam Long clientId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un CLIENT
        if (currentUser.getRole() != Role.CLIENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux clients"));
        }

        // 3. Vérifier que l'ID du client fourni correspond à l'utilisateur connecté
        if (!currentUser.getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à ouvrir un litige pour un autre client"));
        }

        // 4. Récupérer la réservation
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }

        // 5. Vérifier que le client de la réservation correspond bien au client connecté
        if (!reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à ouvrir un litige sur cette réservation"));
        }

        // 6. Vérifier si un litige existe déjà pour cette réservation
        if (litigeRepository.existsByReservationId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Un litige existe déjà pour cette réservation"));
        }

        // 7. Créer et sauvegarder le litige
        Litige litige = new Litige();
        litige.setReservation(reservation);
        litige.setMotif(request.getMotif());
        litige.setStatut("OUVERT");
        litigeRepository.save(litige);

        return ResponseEntity.status(HttpStatus.CREATED).body(litige);
    }

    // Admin : consulter tous les litiges ouverts
    @GetMapping("/litiges/ouverts")
    @Operation(summary = "Consulter tous les litiges ouverts (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des litiges ouverts"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs")
    })
    public ResponseEntity<?> getLitigesOuverts() {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un ADMIN
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux administrateurs"));
        }

        // 3. Récupérer et retourner les litiges ouverts
        List<Litige> litiges = litigeRepository.findByStatut("OUVERT");
        return ResponseEntity.ok(litiges);
    }

    // Admin : résoudre un litige
    @PutMapping("/litiges/{litigeId}")
    @Operation(summary = "Résoudre un litige (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Litige résolu"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Litige non trouvé")
    })
    public ResponseEntity<?> resoudreLitige(@Parameter(description = "ID du litige") @PathVariable Long litigeId,
                                            @Parameter(description = "Décision de résolution") @RequestParam String resolution) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un ADMIN
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux administrateurs"));
        }

        // 3. Récupérer le litige
        Litige litige = litigeRepository.findById(litigeId).orElse(null);
        if (litige == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Litige non trouvé"));
        }

        // 4. Vérifier que le litige est encore ouvert (bonne pratique)
        if (!"OUVERT".equals(litige.getStatut())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Seul un litige ouvert peut être résolu"));
        }

        // 5. Résoudre le litige
        litige.setStatut("RESOLU");
        litige.setResolution(resolution);
        litigeRepository.save(litige);

        return ResponseEntity.ok(litige);
    }

    // -------------------- PAIEMENT SIMULÉ --------------------
    @PostMapping("/{id}/paiement")
    @Operation(summary = "Simuler un paiement (client)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement simulé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (rôle incorrect ou client non autorisé)"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<?> simulerPaiement(@Parameter(description = "ID de la réservation") @PathVariable Long id,
                                             @Parameter(description = "Mode de paiement (mobile_money, cash)") @RequestParam String modePaiement,
                                             @Parameter(description = "ID du client pour vérification") @RequestParam Long clientId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur est bien un CLIENT
        if (currentUser.getRole() != Role.CLIENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux clients"));
        }

        // 3. Vérifier que l'ID du client fourni correspond à l'utilisateur connecté
        if (!currentUser.getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à effectuer un paiement pour un autre client"));
        }

        // 4. Récupérer la réservation
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Réservation non trouvée"));
        }

        // 5. Vérifier que le client de la réservation correspond bien au client connecté
        if (!reservation.getClient().getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à payer cette réservation"));
        }

        // 6. Simuler le paiement
        reservation.setStatut("PAYEE");
        reservationRepository.save(reservation);

        return ResponseEntity.ok(Map.of("message", "Paiement simulé effectué avec " + modePaiement));
    }
}