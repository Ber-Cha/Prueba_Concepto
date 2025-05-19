package com.kafka.provider.springbootprovider.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.provider.springbootprovider.dto.AuthResponse;
import com.kafka.provider.springbootprovider.dto.LoginRequest;
import com.kafka.provider.springbootprovider.dto.RegistroRequest;

import lombok.RequiredArgsConstructor;

// Controlador para autenticación y registro de usuarios
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("registro")
    public ResponseEntity<AuthResponse> registro(@RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registro(request));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
            @RequestHeader("X-API-KEY") String apiKeyHeader) {
        try {
            return ResponseEntity.ok(authService.login(request, apiKeyHeader));
        } catch (IllegalStateException e) {
            // Captura específica para cuando el usuario ya tiene sesión activa
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            // Para otros errores de autenticación
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
            return ResponseEntity.ok("Sesión cerrada exitosamente");
        }
        return ResponseEntity.badRequest().body("Token no válido");
    }
}