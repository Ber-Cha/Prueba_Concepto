package com.kafka.provider.springbootprovider.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String jwt = "JWT";
        final String apiKey = "APIKey";

        return new OpenAPI()
                .info(new Info()
                        .title("API de PPOO2")
                        .version("1.0")
                        .description("Documentación de la API con autenticación JWT y API Key"))
                .addSecurityItem(new SecurityRequirement().addList(jwt))
                .addSecurityItem(new SecurityRequirement().addList(apiKey))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(jwt, new SecurityScheme()
                                .type(Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(In.HEADER)
                                .name("Authorization")
                                .description("Ingrese el token JWT en el formato: Bearer <token>"))
                        .addSecuritySchemes(apiKey, new SecurityScheme()
                                .type(Type.APIKEY)
                                .in(In.HEADER)
                                .name("X-API-KEY")
                                .description("Ingrese su API Key")));
    }
}