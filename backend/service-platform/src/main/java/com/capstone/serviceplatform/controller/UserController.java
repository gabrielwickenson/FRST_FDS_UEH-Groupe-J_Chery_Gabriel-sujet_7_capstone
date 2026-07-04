package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.ChangePasswordRequest;
import com.capstone.serviceplatform.dto.UpdateUserRequest;
import com.capstone.serviceplatform.entity.*;
import com.capstone.serviceplatform.repository.ClientRepository;
import com.capstone.serviceplatform.repository.PrestataireRepository;
import com.capstone.serviceplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "Gestion des profils, tokens FCM et notifications")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PrestataireRepository prestataireRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint pour enregistrer le token FCM
    @PutMapping("/fcm-token")
    @Operation(summary = "Enregistrer le token FCM pour les notifications push")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token mis à jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (vous ne pouvez modifier que votre propre token)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> updateFcmToken(@Parameter(description = "Token FCM reçu de l'application mobile") @RequestParam String fcmToken,
                                            @Parameter(description = "ID de l'utilisateur") @RequestParam Long userId) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'ID de l'utilisateur dans la requête correspond à l'utilisateur connecté
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier le token d'un autre utilisateur"));
        }

        // 3. Mettre à jour le token FCM
        currentUser.setFcmToken(fcmToken);
        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "Token FCM mis à jour"));
    }

    // Vous pouvez ajouter d'autres endpoints utiles (ex: consulter son profil)
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les informations d'un utilisateur (profil)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil utilisateur",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (vous ne pouvez consulter que votre propre profil)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'ID demandé correspond à l'utilisateur connecté
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à consulter le profil d'un autre utilisateur"));
        }

        // 3. Récupérer et retourner le profil (sans mot de passe)
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur non trouvé"));
        }
        user.setMotDePasse(null);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour le profil de l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> updateUser(
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
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier un autre compte"));
        }

        // 3. Mettre à jour les champs communs
        if (request.getNom() != null) currentUser.setNom(request.getNom());
        if (request.getTelephone() != null) currentUser.setTelephone(request.getTelephone());

        // 4. Mettre à jour les champs spécifiques selon le rôle
        if (currentUser.getRole() == Role.CLIENT) {
            // Récupérer l'entité Client associée
            Client client = clientRepository.findById(id).orElse(null);
            if (client != null && request.getAdresseParDefaut() != null) {
                client.setAdresseParDefaut(request.getAdresseParDefaut());
                clientRepository.save(client);
            }
        } else if (currentUser.getRole() == Role.PRESTATAIRE) {
            Prestataire prestataire = prestataireRepository.findById(id).orElse(null);
            if (prestataire != null) {
                if (request.getCompetences() != null) prestataire.setCompetences(request.getCompetences());
                if (request.getTarifHoraire() != null) {
                    prestataire.setTarifHoraire(BigDecimal.valueOf(request.getTarifHoraire()));
                }
                if (request.getZoneIntervention() != null) prestataire.setZoneIntervention(request.getZoneIntervention());
                prestataireRepository.save(prestataire);
            }
        }

        // 5. Sauvegarder l'utilisateur (pour les champs communs)
        userRepository.save(currentUser);

        // 6. Retourner le profil mis à jour (sans mot de passe)
        currentUser.setMotDePasse(null);
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Changer le mot de passe de l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe mis à jour"),
            @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect ou nouveau mot de passe invalide"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (tentative de modification d'un autre compte)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {

        // 1. Vérifier l'authentification
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur modifie son propre mot de passe
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à changer le mot de passe d'un autre compte"));
        }

        // 3. Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getMotDePasse())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ancien mot de passe incorrect"));
        }

        // 4. Vérifier que le nouveau mot de passe est valide (longueur minimale)
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Le nouveau mot de passe doit contenir au moins 6 caractères"));
        }

        // 5. Hasher et sauvegarder le nouveau mot de passe
        currentUser.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));
    }
}