package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.Prestataire;
import com.capstone.serviceplatform.entity.Reservation;
import com.capstone.serviceplatform.repository.PrestataireRepository;
import com.capstone.serviceplatform.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/prestataires")
public class PrestataireController {

    @Autowired
    private PrestataireRepository prestataireRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/recherche")
    public ResponseEntity<List<Prestataire>> rechercherPrestataires(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Double noteMin,
            @RequestParam(required = false) String zone) {

        List<Prestataire> resultats = prestataireRepository.rechercherParFiltres(service, noteMin, zone);
        // Masquer le mot de passe pour la réponse
        resultats.forEach(p -> p.setMotDePasse(null));
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/{id}/statistiques")
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
}