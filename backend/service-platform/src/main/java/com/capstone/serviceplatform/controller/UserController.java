package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "Gestion des profils, tokens FCM et notifications")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private static final long MAX_PHOTO_SIZE_BYTES = 5 * 1024 * 1024;
    private static final List<String> PHOTO_MULTIPART_FIELDS = List.of("file", "photo", "image", "avatar");

    @Autowired
    private UserRepository userRepository;

    @Value("${app.upload.dir:${app.upload-dir:uploads}}")
    private String uploadDir;

    @PutMapping("/fcm-token")
    @Operation(summary = "Enregistrer le token FCM pour les notifications push")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token mis a jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifie"),
            @ApiResponse(responseCode = "403", description = "Non autorise"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouve")
    })
    public ResponseEntity<?> updateFcmToken(
            @Parameter(description = "Token FCM recu de l'application mobile") @RequestParam String fcmToken,
            @Parameter(description = "ID de l'utilisateur") @RequestParam Long userId) {

        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifie"));
        }

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'etes pas autorise a modifier le token d'un autre utilisateur"));
        }

        currentUser.setFcmToken(fcmToken);
        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "Token FCM mis a jour"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les informations d'un utilisateur (profil)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil utilisateur",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifie"),
            @ApiResponse(responseCode = "403", description = "Non autorise"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouve")
    })
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifie"));
        }

        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'etes pas autorise a consulter le profil d'un autre utilisateur"));
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur non trouve"));
        }

        user.setMotDePasse(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader une photo de profil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo mise a jour"),
            @ApiResponse(responseCode = "401", description = "Non authentifie"),
            @ApiResponse(responseCode = "403", description = "Non autorise"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, MultipartHttpServletRequest request) {

        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifie"));
        }

        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'etes pas autorise a modifier la photo d'un autre utilisateur"));
        }

        MultipartFile file = resolvePhotoFile(request);
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Aucun fichier fourni. Champs acceptes: file, photo, image, avatar"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Le fichier doit etre une image"));
        }

        if (file.getSize() > MAX_PHOTO_SIZE_BYTES) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La taille du fichier ne doit pas depasser 5 Mo"));
        }

        try {
            Path uploadDirectory = getUploadDirectory();
            Files.createDirectories(uploadDirectory);

            String fileName = UUID.randomUUID() + resolveExtension(file);
            Path destination = uploadDirectory.resolve(fileName).normalize();
            if (!destination.startsWith(uploadDirectory)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Nom de fichier invalide"));
            }

            file.transferTo(destination);

            String oldPhoto = currentUser.getPhoto();
            String photoUrl = buildPhotoUrl(fileName);
            currentUser.setPhoto(photoUrl);
            User savedUser = userRepository.saveAndFlush(currentUser);
            deleteOldPhoto(oldPhoto);

            return ResponseEntity.ok(Map.of(
                    "photo", savedUser.getPhoto(),
                    "photoUrl", savedUser.getPhoto(),
                    "url", savedUser.getPhoto(),
                    "userId", savedUser.getId(),
                    "message", "Photo mise a jour avec succes"
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'enregistrement du fichier: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/photo")
    @Operation(summary = "Recuperer la photo de profil d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo renvoyee"),
            @ApiResponse(responseCode = "401", description = "Non authentifie"),
            @ApiResponse(responseCode = "403", description = "Non autorise"),
            @ApiResponse(responseCode = "404", description = "Photo non trouvee")
    })
    public ResponseEntity<?> getPhoto(@PathVariable Long id) {

        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Utilisateur non authentifie"));
        }

        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous n'etes pas autorise a voir cette photo"));
        }

        User targetUser = userRepository.findById(id).orElse(null);
        if (targetUser == null || targetUser.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path file = resolveStoredPhotoPath(targetUser.getPhoto());
            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    private MultipartFile resolvePhotoFile(MultipartHttpServletRequest request) {
        for (String fieldName : PHOTO_MULTIPART_FIELDS) {
            MultipartFile file = request.getFile(fieldName);
            if (file != null) {
                return file;
            }
        }
        return request.getFileMap().values().stream().findFirst().orElse(null);
    }

    private Path getUploadDirectory() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private String resolveExtension(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (StringUtils.hasText(extension)) {
            String normalized = extension.toLowerCase(Locale.ROOT);
            if (List.of("jpg", "jpeg", "png", "gif", "webp").contains(normalized)) {
                return "." + normalized;
            }
        }

        String contentType = file.getContentType();
        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }
        if ("image/gif".equalsIgnoreCase(contentType)) {
            return ".gif";
        }
        if ("image/webp".equalsIgnoreCase(contentType)) {
            return ".webp";
        }
        return ".jpg";
    }

    private String buildPhotoUrl(String fileName) {
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();
        } catch (IllegalStateException e) {
            return "/uploads/" + fileName;
        }
    }

    private void deleteOldPhoto(String oldPhoto) {
        Path oldPhotoPath = resolveStoredPhotoPath(oldPhoto);
        if (oldPhotoPath == null) {
            return;
        }
        try {
            Files.deleteIfExists(oldPhotoPath);
        } catch (IOException ignored) {
        }
    }

    private Path resolveStoredPhotoPath(String photo) {
        if (!StringUtils.hasText(photo)) {
            return null;
        }

        String fileName = photo;
        int uploadsIndex = fileName.indexOf("/uploads/");
        if (uploadsIndex >= 0) {
            fileName = fileName.substring(uploadsIndex + "/uploads/".length());
        }

        int queryIndex = fileName.indexOf('?');
        if (queryIndex >= 0) {
            fileName = fileName.substring(0, queryIndex);
        }

        int fragmentIndex = fileName.indexOf('#');
        if (fragmentIndex >= 0) {
            fileName = fileName.substring(0, fragmentIndex);
        }

        fileName = Paths.get(fileName).getFileName().toString();
        Path uploadDirectory = getUploadDirectory();
        Path photoPath = uploadDirectory.resolve(fileName).normalize();
        if (!photoPath.startsWith(uploadDirectory)) {
            return null;
        }
        return photoPath;
    }
}
