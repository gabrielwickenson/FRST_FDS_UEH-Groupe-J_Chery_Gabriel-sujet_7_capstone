package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "Gestion des profils, tokens FCM et notifications")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private MultipartFile resolveUploadedFile(MultipartFile file, MultipartFile photo) {
        if (file != null && !file.isEmpty()) {
            return file;
        }
        if (photo != null && !photo.isEmpty()) {
            return photo;
        }
        return null;
    }

    private Path getUploadDirectoryPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private Path resolveStoredPhotoPath(String storedPhoto) {
        if (storedPhoto == null || storedPhoto.isBlank()) {
            return null;
        }

        String normalized = storedPhoto.trim();
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            try {
                normalized = URI.create(normalized).getPath();
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (normalized.startsWith("uploads/")) {
            normalized = normalized.substring("uploads/".length());
        }

        if (normalized.isBlank()) {
            return null;
        }

        return getUploadDirectoryPath().resolve(normalized).normalize();
    }

    private String buildPublicPhotoUrl(String relativePhotoPath) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(relativePhotoPath)
                .toUriString();
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

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
        User currentUser = getAuthenticatedUser();
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
        User currentUser = getAuthenticatedUser();
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

    @Transactional
    @PostMapping("/{id}/photo")
    @Operation(summary = "Uploader une photo de profil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo mise à jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {

        MultipartFile uploadedFile = resolveUploadedFile(file, photo);

        // 1. Vérifier l'authentification
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur modifie son propre compte
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à modifier la photo d'un autre utilisateur"));
        }

        // 3. Vérifier que le fichier est présent
        if (uploadedFile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Aucun fichier fourni"));
        }

        // 4. Vérifier que le fichier est une image
        String contentType = uploadedFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Le fichier doit être une image"));
        }

        // 5. Vérifier la taille du fichier (max 5 Mo)
        if (uploadedFile.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La taille du fichier ne doit pas dépasser 5 Mo"));
        }

        try {
            Path uploadPath = getUploadDirectoryPath();
            Files.createDirectories(uploadPath);

            // 6. Générer un nom unique pour le fichier
            String originalFilename = uploadedFile.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = System.currentTimeMillis() + "_" + currentUser.getId() + extension;

            // 7. Sauvegarder le fichier
            Path dest = uploadPath.resolve(fileName);
            uploadedFile.transferTo(dest.toFile());

            // 9. Supprimer l'ancienne photo si elle existe (optionnel)
            String oldPhoto = currentUser.getPhoto();
            Path oldFilePath = resolveStoredPhotoPath(oldPhoto);
            if (oldFilePath != null && Files.exists(oldFilePath) && !oldFilePath.equals(dest)) {
                Files.deleteIfExists(oldFilePath);
            }

            // 10. Mettre à jour l'utilisateur
            String relativePhotoPath = "/uploads/" + fileName;
            currentUser.setPhoto(relativePhotoPath);
            System.out.println("📸 Photo sauvegardée : " + currentUser.getPhoto());
            userRepository.save(currentUser);

            String publicPhotoUrl = buildPublicPhotoUrl(relativePhotoPath);

            // 11. Retourner l'URL de la nouvelle photo
            return ResponseEntity.ok(Map.of(
                    "photo", currentUser.getPhoto(),
                    "photoUrl", publicPhotoUrl,
                    "publicPhotoUrl", publicPhotoUrl,
                    "userId", currentUser.getId(),
                    "message", "Photo mise à jour avec succès"
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'enregistrement du fichier: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/photo")
    @Operation(summary = "Récupérer la photo de profil d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo renvoyée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Photo non trouvée")
    })
    public ResponseEntity<?> getPhoto(@PathVariable Long id) {
        // 1. Vérifier l'authentification
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifié"));
        }

        // 2. Vérifier que l'utilisateur a le droit de voir cette photo
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'êtes pas autorisé à voir cette photo"));
        }

        // 3. Récupérer l'utilisateur pour obtenir le chemin de la photo
        User targetUser = userRepository.findById(id).orElse(null);
        if (targetUser == null || targetUser.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 4. Lire le fichier
            String photoPath = targetUser.getPhoto(); // ex: "/uploads/123456.jpg"
            Path file = resolveStoredPhotoPath(photoPath);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                try {
                    String detectedType = Files.probeContentType(file);
                    if (detectedType != null && !detectedType.isBlank()) {
                        mediaType = MediaType.parseMediaType(detectedType);
                    }
                } catch (IOException ignored) {
                    // Fallback to application/octet-stream if the content type cannot be determined.
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
}