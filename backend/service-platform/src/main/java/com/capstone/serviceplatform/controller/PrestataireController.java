package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.Disponibilite;
import com.capstone.serviceplatform.entity.Prestataire;
import com.capstone.serviceplatform.entity.Reservation;
import com.capstone.serviceplatform.repository.DisponibiliteRepository;
import com.capstone.serviceplatform.repository.PrestataireRepository;
import com.capstone.serviceplatform.repository.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<?> getStatistiques(@PathVariable Long id) {
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
        if (prestataire == null) return ResponseEntity.notFound().build();

        // Récupérer les réservations TERMINÉES
        List<Reservation> terminees = reservationRepository.findByPrestataireIdAndStatut(id, "TERMINEE");

        // Revenus totaux
        BigDecimal totalRevenus = terminees.stream()
                .map(Reservation::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Nombre de prestations
        int nbPrestations = terminees.size();

        // Note moyenne (déjà dans prestataire.moyenneNotes, mais on peut recalculer)
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
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<?> ajouterDisponibilite(@Parameter(description = "ID du prestataire") @PathVariable Long id,
                                                  @Valid @RequestBody Disponibilite disponibilite) {
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
        if (prestataire == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Prestataire non trouvé"));
        }
        disponibilite.setPrestataire(prestataire);
        Disponibilite saved = disponibiliteRepository.save(disponibilite);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Lister les disponibilités d'un prestataire
    @GetMapping("/{id}/disponibilites")
    @Operation(summary = "Lister les disponibilités d'un prestataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des disponibilités"),
            @ApiResponse(responseCode = "404", description = "Prestataire non trouvé")
    })
    public ResponseEntity<List<Disponibilite>> getDisponibilites(@PathVariable Long id) {
        Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
        if (prestataire == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(disponibiliteRepository.findByPrestataire(prestataire));
    }

    // Supprimer une disponibilité
    @DeleteMapping("/disponibilites/{disponibiliteId}")
    @Operation(summary = "Supprimer une disponibilité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Disponibilité non trouvée")
    })
    public ResponseEntity<?> supprimerDisponibilite(@PathVariable Long disponibiliteId) {
        if (!disponibiliteRepository.existsById(disponibiliteId)) {
            return ResponseEntity.notFound().build();
        }
        disponibiliteRepository.deleteById(disponibiliteId);
        return ResponseEntity.noContent().build();
    }
}