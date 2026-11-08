package com.webclient.practica2.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("API REST Nivel 3 de Richardson - Gestión de Entrenadores Deportivos")
                        .version("1.0.0")
                        .description("API RESTful desarrollada con Spring Boot, Spring HATEOAS, Spring Security y JWT. " +
                                "Cumple con el Nivel 3 del Modelo de Madurez de Richardson e implementa control de acceso basado en roles (ADMIN y CONSULTA).")
                        .contact(new Contact()
                                .name("Estudiante - Práctica Integradora")
                                .email("alumno@universidad.edu"))
                        .license(new License().name("Apache 2.0").url("https://spring.io/")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese su token JWT generado desde el endpoint /api/auth/login")));
    }
}

