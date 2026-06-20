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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Inscription et connexion (public)")
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

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur (client ou prestataire)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email déjà utilisé"));
        }

        User savedUser = null;

        if (request.getRole() == Role.CLIENT) {
            Client client = new Client();
            client.setNom(request.getNom());
            client.setEmail(request.getEmail());
            client.setMotDePasse(passwordEncoder.encode(request.getMotDePasse())); // Haché
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
            prestataire.setMotDePasse(passwordEncoder.encode(request.getMotDePasse())); // Haché
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
            User user = new User();
            user.setNom(request.getNom());
            user.setEmail(request.getEmail());
            user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
            user.setTelephone(request.getTelephone());
            user.setPhoto(request.getPhoto());
            user.setDateInscription(new Date());
            user.setRole(Role.ADMIN);
            savedUser = userRepository.save(user);
        }

        savedUser.setMotDePasse(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion et récupération du token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie, token JWT renvoyé"),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email incorrect"));
        }
        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Mot de passe incorrect"));
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("nom", user.getNom());
        response.put("role", user.getRole().name());
        response.put("token", token);

        user.setMotDePasse(null);
        return ResponseEntity.ok(response);
    }
}