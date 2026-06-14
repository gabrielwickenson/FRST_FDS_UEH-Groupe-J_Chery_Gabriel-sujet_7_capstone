package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Endpoint pour enregistrer le token FCM
    @PutMapping("/fcm-token")
    public ResponseEntity<?> updateFcmToken(@RequestParam String fcmToken, @RequestParam Long userId) {
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
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setMotDePasse(null); // masquer le mot de passe
        return ResponseEntity.ok(user);
    }
}