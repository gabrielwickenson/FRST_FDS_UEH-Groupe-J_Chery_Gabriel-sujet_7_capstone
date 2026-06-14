package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.ReservationRequest;
import com.capstone.serviceplatform.entity.*;
import com.capstone.serviceplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
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

        // Vérifier si le créneau est déjà pris (pour ce prestataire)
        boolean conflit = reservationRepository.existsConflit(prestataire.getId(), request.getDateHeure());
        if (conflit) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Le prestataire est déjà réservé à cette date/heure"));
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
        saved.setClient(null); // éviter les cycles JSON (optionnel)
        saved.setPrestataire(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}