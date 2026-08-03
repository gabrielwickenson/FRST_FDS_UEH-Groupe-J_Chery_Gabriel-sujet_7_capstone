package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.AvisRequest;
import com.capstone.serviceplatform.dto.DailyRevenue;
import com.capstone.serviceplatform.entity.*;
import com.capstone.serviceplatform.repository.AvisRepository;
import com.capstone.serviceplatform.repository.ClientRepository;
import com.capstone.serviceplatform.repository.DisponibiliteRepository;
import com.capstone.serviceplatform.repository.PrestataireRepository;
import com.capstone.serviceplatform.repository.ReservationRepository;
import com.capstone.serviceplatform.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.core.context.SecurityContextHolder;
import com.capstone.serviceplatform.dto.AvailabilityRequest;
import com.capstone.serviceplatform.dto.DailyRevenue;
import com.capstone.serviceplatform.dto.UpdateUserRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.text.SimpleDateFormat;
import java.util.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestataires")
@Tag(name = "Prestataires", description = "Gestion des prestataires, disponibilités et statistiques")
@SecurityRequirement(name = "Bearer Authentication")
public class PrestataireController {

    @Autowired
    private PrestataireRepository prestataireRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private DisponibiliteRepository disponibiliteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AvisRepository avisRepository;
    @Autowired
    private ClientRepository clientRepository;

    // Récupère la ligne `prestataire` de l'utilisateur, et la provisionne à
    // la volée si elle manque (compte dont le rôle affiché est PRESTATAIRE
    // mais qui n'a, pour une raison ou une autre, jamais eu de ligne fille
    // créée — voir PrestataireRepository.provisionPrestataireRow). Utilisé
    // partout où l'appelant a déjà été vérifié comme étant le propriétaire
    // du compte `id`, pour ne pas renvoyer un 404 "Prestataire non trouvé"
    // à répétition sur un compte par ailleurs valide.
    private Prestataire resolvePrestataire(Long id) {
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
        if (prestataire == null && userRepository.existsById(id)) {
            prestataireRepository.provisionPrestataireRow(id);
            prestataire = prestataireRepository.findById(id).orElse(null);
        }
        return prestataire;
    }

    @GetMapping("/recherche")
    @Operation(summary = "Recherche publique de prestataires (filtres optionnels)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des prestataires filtrés",
                    content = @Content(schema = @Schema(implementation = Prestataire.class)))
    })
    public ResponseEntity<List<Prestataire>> rechercherPrestataires(
            @Parameter(description = "Nom du service (ex: plomberie)") @RequestParam(required = false) String service,
            @Parameter(description = "Note minimale (ex: 4)") @RequestParam(required = false) Double noteMin,
            @Parameter(description = "Zone d'intervention (ex: Pétion-Ville)") @RequestParam(required = false) String zone) {

        List<Prestataire> resultats = prestataireRepository.rechercherParFiltres(service, noteMin, zone).stream()
                // Un compte suspendu par un administrateur (voir
                // AdminController.updateStatutPrestataire) ne doit plus être
                // découvrable ni réservable publiquement, même s'il continue
                // de matcher les filtres de recherche.
                .filter(p -> !"SUSPENDU".equals(p.getStatutCompte()))
                .collect(Collectors.toList());
        // Masquer le mot de passe pour la réponse
        resultats.forEach(p -> p.setMotDePasse(null));
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/{id}/statistiques")
    @Operation(summary = "Statistiques d'un prestataire (revenus, nombre prestations, note moyenne)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques retournées"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit (rôle incorrect ou prestataire non autorisé)"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> getStatistiques(@PathVariable Long id) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        // 2. Vérifier que l'utilisateur existe
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 3. Vérifier que l'utilisateur est bien un PRESTATAIRE
        if (currentUser.getRole() != Role.PRESTATAIRE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux prestataires"));
        }

        // 4. Vérifier que l'ID dans l'URL correspond à l'ID du prestataire authentifié
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à consulter les statistiques d'un autre prestataire"));
        }

        // 5. Récupérer le prestataire (le provisionner si la ligne manque)
        Prestataire prestataire = resolvePrestataire(id);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // --- Revenus : toute réservation où l'argent a effectivement changé
        // de main (payée, en cours après paiement, ou terminée), à
        // l'exclusion des réservations annulées. Auparavant on ne comptait
        // que le statut TERMINEE, ce qui ignorait les réservations payées
        // mais pas encore marquées terminées par le prestataire.
        List<Reservation> toutes = reservationRepository.findByPrestataireId(id);
        List<String> statutsPayes = Arrays.asList("PAYEE", "EN_COURS", "TERMINEE");
        BigDecimal totalRevenus = toutes.stream()
                .filter(r -> statutsPayes.contains(r.getStatut()))
                .map(Reservation::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long nbTerminees = toutes.stream().filter(r -> "TERMINEE".equals(r.getStatut())).count();
        Double moyenne = prestataire.getMoyenneNotes() != null ? prestataire.getMoyenneNotes().doubleValue() : 0.0;
        int nombreAvis = avisRepository.findByPrestataireId(id).size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenus", totalRevenus);
        stats.put("nombrePrestations", toutes.size());
        stats.put("nombreTerminees", nbTerminees);
        stats.put("noteMoyenne", moyenne);
        stats.put("nombreAvis", nombreAvis);

        return ResponseEntity.ok(stats);
    }

    // Ajouter une disponibilité
    @PostMapping("/{id}/disponibilites")
    @Operation(summary = "Ajouter une disponibilité pour un prestataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disponibilité ajoutée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit (rôle incorrect ou prestataire non autorisé)"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> ajouterDisponibilite(@Parameter(description = "ID du prestataire") @PathVariable Long id,
                                                  @Valid @RequestBody Disponibilite disponibilite) {

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

        // 3. Vérifier que l'ID du prestataire dans l'URL correspond à l'ID de l'utilisateur connecté
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à ajouter des disponibilités pour un autre prestataire"));
        }

        // 4. Récupérer le prestataire (le provisionner si la ligne manque)
        Prestataire prestataire = resolvePrestataire(id);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // 5. Créer et sauvegarder la disponibilité
        disponibilite.setPrestataire(prestataire);
        Disponibilite saved = disponibiliteRepository.save(disponibilite);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Lister les disponibilités d'un prestataire
    @GetMapping("/{id}/disponibilites")
    @Operation(summary = "Lister les disponibilités d'un prestataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des disponibilités"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit (rôle incorrect ou prestataire non autorisé)"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> getDisponibilites(@PathVariable Long id) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        // 2. Vérifier que l'utilisateur existe
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 3. Vérifier que l'utilisateur est bien un PRESTATAIRE
        if (currentUser.getRole() != Role.PRESTATAIRE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux prestataires"));
        }

        // 4. Vérifier que l'ID dans l'URL correspond à l'ID du prestataire authentifié
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à consulter les disponibilités d'un autre prestataire"));
        }

        // 5. Récupérer le prestataire (le provisionner si la ligne manque)
        Prestataire prestataire = resolvePrestataire(id);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // --- Logique métier inchangée ---
        List<Disponibilite> disponibilites = disponibiliteRepository.findByPrestataire(prestataire);
        return ResponseEntity.ok(disponibilites);
    }

    // Supprimer une disponibilité
    @DeleteMapping("/disponibilites/{disponibiliteId}")
    @Operation(summary = "Supprimer une disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supprimée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit (rôle incorrect ou prestataire non autorisé)"),
            @ApiResponse(responseCode = "404", description = "Disponibilité non trouvée")
    })
    public ResponseEntity<?> supprimerDisponibilite(@PathVariable Long disponibiliteId) {

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

        // 3. Récupérer la disponibilité pour vérifier son propriétaire
        Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId).orElse(null);
        if (disponibilite == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Disponibilité non trouvée"));
        }

        // 4. Vérifier que le prestataire associé à la disponibilité est bien l'utilisateur connecté
        if (!disponibilite.getPrestataire().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à supprimer cette disponibilité"));
        }

        // 5. Supprimer la disponibilité
        disponibiliteRepository.deleteById(disponibiliteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/availability")
    @Operation(summary = "Mettre à jour la disponibilité du prestataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (vous ne pouvez modifier que votre propre disponibilité)"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> updateAvailability(
            @PathVariable Long id,
            @RequestBody AvailabilityRequest request) {

        // 1. Vérifier l'authentification
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur modifie son propre compte
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier la disponibilité d'un autre prestataire"));
        }

        // 3. Récupérer le prestataire et mettre à jour (le provisionner si la ligne manque)
        Prestataire prestataire = resolvePrestataire(id);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        prestataire.setDisponible(request.isDisponible());
        prestataireRepository.save(prestataire);

        return ResponseEntity.ok(Map.of(
                "message", "Disponibilité mise à jour",
                "disponible", request.isDisponible()
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour le profil du prestataire connecté (tarif horaire, compétences, zone, téléphone, nom)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (vous ne pouvez modifier que votre propre profil)"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        // 1. Vérifier l'authentification
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur modifie son propre profil
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier le profil d'un autre prestataire"));
        }

        // 3. Récupérer le prestataire (le provisionner si la ligne manque)
        Prestataire prestataire = resolvePrestataire(id);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // 4. Mettre à jour uniquement les champs fournis
        if (request.getNom() != null) {
            prestataire.setNom(request.getNom());
        }
        if (request.getTelephone() != null) {
            prestataire.setTelephone(request.getTelephone());
        }
        if (request.getCompetences() != null) {
            prestataire.setCompetences(request.getCompetences());
        }
        if (request.getTarifHoraire() != null) {
            prestataire.setTarifHoraire(BigDecimal.valueOf(request.getTarifHoraire()));
        }
        if (request.getZoneIntervention() != null) {
            prestataire.setZoneIntervention(request.getZoneIntervention());
        }
        if (request.getBio() != null) {
            prestataire.setBio(request.getBio());
        }

        Prestataire updated = prestataireRepository.save(prestataire);
        updated.setMotDePasse(null);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/revenue/week")
    @Operation(summary = "Revenus des 7 derniers jours pour un prestataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des revenus journaliers"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (vous ne pouvez consulter que vos propres revenus)"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> getWeeklyRevenue(@PathVariable Long id) {

        // 1. Vérifier l'authentification
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur consulte ses propres revenus
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à consulter les revenus d'un autre prestataire"));
        }

        // 3. Vérifier que le prestataire existe (le provisionner si la ligne manque)
        Prestataire prestataire = resolvePrestataire(id);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // 4. Revenus des 7 derniers jours, fenêtre se terminant AUJOURD'HUI.
        // On regroupe par DATE DE PAIEMENT (datePaiement, renseignée lors de
        // POST /reservations/{id}/paiement) et non par dateHeure : la
        // dateHeure est la date du rendez-vous, souvent future, ce qui
        // laissait le graphe éternellement à zéro même après des paiements
        // réels. Repli sur dateHeure pour les anciens paiements enregistrés
        // avant l'ajout de la colonne datePaiement. Libellés de jour fixes
        // (indépendants de la locale du JVM).
        String[] joursAbbr = {"lun.", "mar.", "mer.", "jeu.", "ven.", "sam.", "dim."};
        List<String> statutsPayes = Arrays.asList("PAYEE", "EN_COURS", "TERMINEE");
        LocalDate today = LocalDate.now();

        List<Reservation> payees = reservationRepository.findByPrestataireId(id).stream()
                .filter(r -> statutsPayes.contains(r.getStatut()))
                .toList();

        List<DailyRevenue> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            BigDecimal total = payees.stream()
                    .filter(r -> {
                        LocalDateTime effective = r.getDatePaiement() != null ? r.getDatePaiement() : r.getDateHeure();
                        return effective != null && effective.toLocalDate().equals(date);
                    })
                    .map(Reservation::getMontant)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String dayLabel = joursAbbr[date.getDayOfWeek().getValue() - 1];
            result.add(new DailyRevenue(dayLabel, total));
        }

        return ResponseEntity.ok(result);
    }

    // -------------------- AVIS SUR LE PROFIL --------------------
    // Contrairement à POST /api/reservations/{id}/avis (qui exige une
    // réservation TERMINEE précise), cet endpoint permet à n'importe quel
    // compte connecté de laisser un avis directement sur le profil d'un
    // prestataire, sans réservation associée.
    @GetMapping("/{id}/avis")
    @Operation(summary = "Récupérer tous les avis d'un prestataire (avis de profil + avis de réservation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des avis"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> getAvisDuProfil(@PathVariable Long id) {
        if (!prestataireRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }
        List<Avis> avis = avisRepository.findByPrestataireId(id);
        // Ne pas exposer les mots de passe des auteurs
        avis.forEach(a -> {
            if (a.getClient() != null) {
                a.getClient().setMotDePasse(null);
            }
        });
        return ResponseEntity.ok(avis);
    }

    @PostMapping("/{id}/avis")
    @Operation(summary = "Laisser un avis directement sur le profil d'un prestataire (sans réservation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avis créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou avis sur son propre profil"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (vous ne pouvez laisser un avis qu'en votre nom)"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> laisserAvisSurProfil(@PathVariable Long id,
                                                   @RequestBody @Valid AvisRequest avisRequest,
                                                   @RequestParam Long clientId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Seule l'identité compte : un compte PRESTATAIRE peut aussi
        // laisser un avis en tant que "client" sur un autre prestataire.
        if (!currentUser.getId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à laisser un avis pour un autre utilisateur"));
        }

        // 3. On ne peut pas laisser un avis sur son propre profil
        if (id.equals(clientId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Vous ne pouvez pas laisser un avis sur votre propre profil"));
        }

        // 4. Récupérer le prestataire évalué
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // 5. Récupérer (ou provisionner) la ligne `client` de l'auteur — même
        // mécanisme que pour la création de réservation, afin qu'un compte
        // PRESTATAIRE puisse laisser un avis sans ligne `client` préexistante.
        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            if (!userRepository.existsById(clientId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Utilisateur non trouvé"));
            }
            clientRepository.provisionClientRow(clientId);
            client = clientRepository.findById(clientId).orElse(null);
        }

        // 6. Créer et sauvegarder l'avis
        Avis avis = new Avis();
        avis.setPrestataire(prestataire);
        avis.setClient(client);
        avis.setNote(avisRequest.getNote());
        avis.setCommentaire(avisRequest.getCommentaire());
        avis.setDate(new Date());
        avisRepository.save(avis);

        // 7. Mettre à jour la moyenne du prestataire
        List<Avis> avisList = avisRepository.findByPrestataireId(prestataire.getId());
        double moyenne = avisList.stream().mapToInt(Avis::getNote).average().orElse(0.0);
        prestataire.setMoyenneNotes(BigDecimal.valueOf(moyenne));
        prestataireRepository.save(prestataire);

        avis.setPrestataire(null);
        if (avis.getClient() != null) {
            avis.getClient().setMotDePasse(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(avis);
    }
}