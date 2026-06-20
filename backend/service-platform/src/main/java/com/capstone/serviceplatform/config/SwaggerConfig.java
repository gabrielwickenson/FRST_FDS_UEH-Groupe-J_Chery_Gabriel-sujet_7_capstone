package com.capstone.serviceplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plateforme de Services à la Demande - API")
                        .version("1.0.0")
                        .description("API REST pour le projet Capstone (Sujet 7) – Gestion des réservations, prestataires, clients, évaluations et litiges.")
                        .contact(new Contact()
                                .name("Gabriel Wickenson")
                                .email("gabrielwickenson18@gmail.com")
                                .url("https://github.com/gabrielwickenson")))
                // Ajoute un bouton "Authorize" pour JWT dans Swagger UI
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Collez votre token JWT ici (ex: eyJhbGciOiJIUzM4NCJ9...)")
                        )
                );
    }
}