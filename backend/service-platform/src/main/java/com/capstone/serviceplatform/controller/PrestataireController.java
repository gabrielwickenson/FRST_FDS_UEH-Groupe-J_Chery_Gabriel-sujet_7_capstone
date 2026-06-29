package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.*;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        List<Prestataire> resultats = prestataireRepository.rechercherParFiltres(service, noteMin, zone);
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

        // 5. Récupérer le prestataire (optionnel, car on a déjà l'utilisateur)
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Prestataire non trouvé"));
        }

        // --- Logique métier inchangée ---
        List<Reservation> terminees = reservationRepository.findByPrestataireIdAndStatut(id, "TERMINEE");
        BigDecimal totalRevenus = terminees.stream()
                .map(Reservation::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int nbPrestations = terminees.size();
        Double moyenne = prestataire.getMoyenneNotes() != null ? prestataire.getMoyenneNotes().doubleValue() : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenus", totalRevenus);
        stats.put("nombrePrestations", nbPrestations);
        stats.put("noteMoyenne", moyenne);

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

        // 4. Récupérer le prestataire (optionnel, car on a déjà l'utilisateur)
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
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

        // 5. Récupérer le prestataire (optionnel)
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
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
}