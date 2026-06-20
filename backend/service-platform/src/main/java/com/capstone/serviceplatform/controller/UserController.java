package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.capstone.serviceplatform.entity.Notification;
import com.capstone.serviceplatform.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    private NotificationRepository notificationRepository;

    // Endpoint pour enregistrer le token FCM
    @PutMapping("/fcm-token")
    @Operation(summary = "Enregistrer le token FCM pour les notifications push")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token mis à jour"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> updateFcmToken(@Parameter(description = "Token FCM reçu de l'application mobile") @RequestParam String fcmToken,
                                            @Parameter(description = "ID de l'utilisateur") @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setFcmToken(fcmToken);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Token FCM mis à jour"));
    }

    // Vous pouvez ajouter d'autres endpoints utiles (ex: consulter son profil)
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les informations d'un utilisateur (profil)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil utilisateur",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setMotDePasse(null); // masquer le mot de passe
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/notifications")
    @Operation(summary = "Historique des notifications d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des notifications",
                    content = @Content(schema = @Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        List<Notification> notifs = notificationRepository.findByUserIdOrderByDateDesc(userId);
        return ResponseEntity.ok(notifs);
    }
}