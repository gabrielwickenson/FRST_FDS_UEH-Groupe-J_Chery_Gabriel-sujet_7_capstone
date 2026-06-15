package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.LoginRequest;
import com.capstone.serviceplatform.dto.RegisterRequest;
import com.capstone.serviceplatform.entity.*;
import com.capstone.serviceplatform.repository.*;
import com.capstone.serviceplatform.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
        }
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole(), "id", user.getId()));
    }

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
            client.setMotDePasse(passwordEncoder.encode(request.getMotDePasse())); // Hachage
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
            prestataire.setMotDePasse(passwordEncoder.encode(request.getMotDePasse())); // Hachage
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
            user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse())); // Hachage
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