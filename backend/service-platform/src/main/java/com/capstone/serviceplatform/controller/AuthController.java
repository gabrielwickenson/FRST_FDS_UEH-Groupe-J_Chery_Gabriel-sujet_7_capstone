package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.entity.Role;
import com.capstone.serviceplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email déjà utilisé");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        user.setDateInscription(new Date());
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }

        User savedUser = userRepository.save(user);
        savedUser.setMotDePasse(null); // ne pas renvoyer le mot de passe
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}