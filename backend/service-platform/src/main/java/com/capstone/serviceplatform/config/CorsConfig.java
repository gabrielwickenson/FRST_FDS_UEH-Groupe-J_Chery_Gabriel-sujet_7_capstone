package com.capstone.serviceplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Configuration CORS globale. Auparavant, seul AuthController exposait
// @CrossOrigin("http://localhost:5173"), ce qui bloquait silencieusement
// (côté navigateur) toutes les requêtes du frontend vers les autres
// contrôleurs (prestataires, services, réservations, utilisateurs) : le
// serveur traitait bien la requête (visible dans les logs/Hibernate), mais
// la réponse n'avait pas les en-têtes CORS attendus, donc le navigateur la
// rejetait avant que le JS ne puisse la lire — d'où des erreurs "Impossible
// de charger..." côté frontend malgré un backend qui fonctionnait.
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
