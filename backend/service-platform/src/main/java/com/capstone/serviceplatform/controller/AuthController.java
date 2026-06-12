package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.RegisterRequest;
import com.capstone.serviceplatform.entity.*;
import com.capstone.serviceplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PrestataireRepository prestataireRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email déjà utilisé");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        User savedUser = null;

        if (request.getRole() == Role.CLIENT) {
            Client client = new Client();
            client.setNom(request.getNom());
            client.setEmail(request.getEmail());
            client.setMotDePasse(request.getMotDePasse());
            client.setTelephone(request.getTelephone());
            client.setPhoto(request.getPhoto());
            client.setDateInscription(new Date());
            client.setRole(Role.CLIENT);
            client.setAdresseParDefaut(request.getAdresseParDefaut());
            savedUser = clientRepository.save(client);
        }
        else if (request.getRole() == Role.PRESTATAIRE) {
            Prestataire prestataire = new Prestataire();
            prestataire.setNom(request.getNom());
            prestataire.setEmail(request.getEmail());
            prestataire.setMotDePasse(request.getMotDePasse());
            prestataire.setTelephone(request.getTelephone());
            prestataire.setPhoto(request.getPhoto());
            prestataire.setDateInscription(new Date());
            prestataire.setRole(Role.PRESTATAIRE);
            prestataire.setCompetences(request.getCompetences());
            if (request.getTarifHoraire() != null) {
                prestataire.setTarifHoraire(BigDecimal.valueOf(request.getTarifHoraire()));
            }
            prestataire.setZoneIntervention(request.getZoneIntervention());
            prestataire.setMoyenneNotes(BigDecimal.ZERO);
            savedUser = prestataireRepository.save(prestataire);
        }
        else {
            // ADMIN
            User user = new User();
            user.setNom(request.getNom());
            user.setEmail(request.getEmail());
            user.setMotDePasse(request.getMotDePasse());
            user.setTelephone(request.getTelephone());
            user.setPhoto(request.getPhoto());
            user.setDateInscription(new Date());
            user.setRole(Role.ADMIN);
            savedUser = userRepository.save(user);
        }

        // Ne pas renvoyer le mot de passe
        savedUser.setMotDePasse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}