package com.capstone.serviceplatform.controller;

import com.capstone.serviceplatform.dto.RegisterRequest;
import com.capstone.serviceplatform.entity.Client;
import com.capstone.serviceplatform.entity.Prestataire;
import com.capstone.serviceplatform.entity.Role;
import com.capstone.serviceplatform.repository.ClientRepository;
import com.capstone.serviceplatform.repository.PrestataireRepository;
import com.capstone.serviceplatform.repository.UserRepository;
import com.capstone.serviceplatform.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PrestataireRepository prestataireRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testRegister_ClientSuccess() {
        // 1. Préparation
        RegisterRequest request = new RegisterRequest();
        request.setEmail("client@test.com");
        request.setMotDePasse("password");
        request.setRole(Role.CLIENT);
        request.setNom("Jean Client");
        request.setAdresseParDefaut("Rue 123");

        when(userRepository.existsByEmail("client@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(jwtUtils.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");

        // 2. Exécution
        ResponseEntity<?> response = authController.register(request);

        // 3. Vérification — l'inscription renvoie désormais un token (comme
        // /login) en plus des infos de base, pour connecter automatiquement
        // l'utilisateur dès son inscription.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("fake-jwt-token", body.get("token"));
        assertEquals("client@test.com", body.get("email"));
        assertEquals("Jean Client", body.get("nom"));
        assertEquals("CLIENT", body.get("role"));
        Client utilisateur = (Client) body.get("utilisateur");
        assertNotNull(utilisateur);
        assertEquals("Rue 123", utilisateur.getAdresseParDefaut());
    }

    @Test
    public void testRegister_PrestataireSuccess() {
        // 1. Préparation
        RegisterRequest request = new RegisterRequest();
        request.setEmail("presta@test.com");
        request.setMotDePasse("password");
        request.setRole(Role.PRESTATAIRE);
        request.setNom("Marie Presta");
        request.setCompetences("Plomberie");
        request.setTarifHoraire(500.0);
        request.setZoneIntervention("Pétion-Ville");

        when(userRepository.existsByEmail("presta@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(prestataireRepository.save(any(Prestataire.class))).thenAnswer(invocation -> {
            Prestataire p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });
        when(jwtUtils.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");

        // 2. Exécution
        ResponseEntity<?> response = authController.register(request);

        // 3. Vérification
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("fake-jwt-token", body.get("token"));
        assertEquals("presta@test.com", body.get("email"));
        assertEquals("Marie Presta", body.get("nom"));
        assertEquals("PRESTATAIRE", body.get("role"));
        Prestataire utilisateur = (Prestataire) body.get("utilisateur");
        assertNotNull(utilisateur);
        assertEquals("Plomberie", utilisateur.getCompetences());
        assertEquals(500.0, utilisateur.getTarifHoraire().doubleValue());
    }

    @Test
    public void testRegister_EmailAlreadyExists() {
        // 1. Préparation
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@test.com");
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        // 2. Exécution
        ResponseEntity<?> response = authController.register(request);

        // 3. Vérification
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}