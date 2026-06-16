package com.capstone.serviceplatform.config;

import com.capstone.serviceplatform.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Accès publics
                        .requestMatchers("/api/auth/**", "/api/services").permitAll()
                        // Client : on utilise * pour l'id (un seul segment)
                        .requestMatchers("/api/reservations/client/**").hasAuthority("CLIENT")
                        .requestMatchers("/api/reservations/*/avis").hasAuthority("CLIENT")
                        .requestMatchers("/api/reservations/*/litige").hasAuthority("CLIENT")
                        .requestMatchers("/api/reservations/*/paiement").hasAuthority("CLIENT")
                        // Prestataire
                        .requestMatchers("/api/prestataires/**").hasAuthority("PRESTATAIRE")
                        //.requestMatchers("/api/prestataires/**").permitAll()
                        .requestMatchers("/api/reservations/prestataire/**").hasAuthority("PRESTATAIRE")
                        .requestMatchers("/api/reservations/*/statut").hasAuthority("PRESTATAIRE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}