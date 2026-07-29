package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.Prestataire;
import com.capstone.serviceplatform.entity.Reservation;
import com.capstone.serviceplatform.entity.Role;
import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.repository.LitigeRepository;
import com.capstone.serviceplatform.repository.ReservationRepository;
import com.capstone.serviceplatform.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// Tableau de bord "vue plateforme" réservé aux comptes ADMIN, exposé
// uniquement via /#/administration côté frontend. Mêmes conventions de
// sécurité que le reste de l'API (vérification manuelle du rôle dans chaque
// méthode, pas de @PreAuthorize) pour rester cohérent avec les autres
// contrôleurs.
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration", description = "Statistiques et gestion réservées aux administrateurs")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private LitigeRepository litigeRepository;

    private static final List<String> STATUTS_PAYES = Arrays.asList("PAYEE", "EN_COURS", "TERMINEE");
    private static final String[] MOIS_ABBR = {
            "Jan.", "Fév.", "Mar.", "Avr.", "Mai", "Juin",
            "Juil.", "Août", "Sep.", "Oct.", "Nov.", "Déc."
    };

    private ResponseEntity<?> adminCheck() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux administrateurs"));
        }
        return null;
    }

    private BigDecimal sommeSurMois(List<Reservation> reservations, YearMonth mois) {
        return reservations.stream()
                .filter(r -> r.getDateHeure() != null && YearMonth.from(r.getDateHeure()).equals(mois))
                .map(Reservation::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @GetMapping("/kpis")
    @Operation(summary = "Indicateurs clés de la plateforme (admin)")
    public ResponseEntity<?> getKpis() {
        ResponseEntity<?> err = adminCheck();
        if (err != null) return err;

        long totalClients = userRepository.countByRole(Role.CLIENT);
        long totalPrestataires = userRepository.countByRole(Role.PRESTATAIRE);
        long totalReservations = reservationRepository.count();

        List<Reservation> payees = reservationRepository.findByStatutIn(STATUTS_PAYES);
        BigDecimal totalRevenus = payees.stream()
                .map(Reservation::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth moisCourant = YearMonth.now();
        YearMonth moisPrecedent = moisCourant.minusMonths(1);
        BigDecimal revenusMoisCourant = sommeSurMois(payees, moisCourant);
        BigDecimal revenusMoisPrecedent = sommeSurMois(payees, moisPrecedent);
        Double deltaRevenusPct = null;
        if (revenusMoisPrecedent.compareTo(BigDecimal.ZERO) > 0) {
            deltaRevenusPct = revenusMoisCourant.subtract(revenusMoisPrecedent)
                    .divide(revenusMoisPrecedent, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        long litigesOuverts = litigeRepository.findByStatut("OUVERT").size();

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalClients", totalClients);
        kpis.put("totalPrestataires", totalPrestataires);
        kpis.put("totalUtilisateurs", totalClients + totalPrestataires);
        kpis.put("totalReservations", totalReservations);
        kpis.put("totalRevenus", totalRevenus);
        kpis.put("revenusMoisCourant", revenusMoisCourant);
        kpis.put("revenusMoisPrecedent", revenusMoisPrecedent);
        kpis.put("deltaRevenusPct", deltaRevenusPct);
        kpis.put("litigesOuverts", litigesOuverts);

        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/revenus-mensuels")
    @Operation(summary = "Revenus des 6 derniers mois, plateforme entière (admin)")
    public ResponseEntity<?> getRevenusMensuels() {
        ResponseEntity<?> err = adminCheck();
        if (err != null) return err;

        List<Reservation> payees = reservationRepository.findByStatutIn(STATUTS_PAYES);
        YearMonth moisCourant = YearMonth.now();

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mois = moisCourant.minusMonths(i);
            Map<String, Object> point = new HashMap<>();
            point.put("mois", MOIS_ABBR[mois.getMonthValue() - 1]);
            point.put("montant", sommeSurMois(payees, mois));
            result.add(point);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/top-categories")
    @Operation(summary = "Répartition des revenus par catégorie de service (admin)")
    public ResponseEntity<?> getTopCategories() {
        ResponseEntity<?> err = adminCheck();
        if (err != null) return err;

        List<Reservation> payees = reservationRepository.findByStatutIn(STATUTS_PAYES);
        Map<String, BigDecimal> parCategorie = new LinkedHashMap<>();
        for (Reservation r : payees) {
            if (r.getService() == null || r.getMontant() == null) continue;
            String cat = r.getService().getCategorie();
            if (cat == null || cat.isBlank()) cat = "Autre";
            parCategorie.merge(cat, r.getMontant(), BigDecimal::add);
        }
        BigDecimal total = parCategorie.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> result = parCategorie.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("categorie", e.getKey());
                    row.put("montant", e.getValue());
                    double pct = total.compareTo(BigDecimal.ZERO) > 0
                            ? e.getValue().divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    row.put("pourcentage", pct);
                    return row;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    @Operation(summary = "Liste de tous les utilisateurs de la plateforme (admin)")
    public ResponseEntity<?> getUsers() {
        ResponseEntity<?> err = adminCheck();
        if (err != null) return err;

        List<User> users = userRepository.findAllByOrderByDateInscriptionDesc();
        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", u.getId());
            row.put("nom", u.getNom());
            row.put("email", u.getEmail());
            row.put("telephone", u.getTelephone());
            row.put("role", u.getRole() != null ? u.getRole().name() : null);
            row.put("dateInscription", u.getDateInscription());
            if (u instanceof Prestataire p) {
                row.put("disponible", p.getDisponible());
                row.put("moyenneNotes", p.getMoyenneNotes());
                row.put("competences", p.getCompetences());
                row.put("nombreAvis", p.getNombreAvis());
            }
            return row;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
