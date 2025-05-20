package com.kafka.provider.springbootprovider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kafka.provider.springbootprovider.Entity.Persona;
import com.kafka.provider.springbootprovider.Entity.Usuario;
import com.kafka.provider.springbootprovider.Entity.UsuarioPK;
import com.kafka.provider.springbootprovider.JWT.ActiveTokenService;
import com.kafka.provider.springbootprovider.JWT.JwtService;
import com.kafka.provider.springbootprovider.dto.AuthResponse;
import com.kafka.provider.springbootprovider.dto.LoginRequest;
import com.kafka.provider.springbootprovider.dto.RegistroRequest;
import com.kafka.provider.springbootprovider.repository.IPersonaRepository;
import com.kafka.provider.springbootprovider.repository.IUsuarioRepository;
import com.kafka.provider.springbootprovider.service.KafkaProducerService;
import com.kafka.provider.springbootprovider.service_impl.ApiKeyGenerator;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final IUsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final IPersonaRepository personaRepository;
        private final AuthenticationManager authenticationManager;
        @Autowired
        private KafkaProducerService kafkaProducerService;
        Logger log;
        private final ActiveTokenService activeTokenService;

        public AuthResponse login(LoginRequest request, String apikeyHeader) {
                try {
                        // Autenticación básica
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getLogin(),
                                                        request.getPassword()));

                        // Verificar API Key
                        Usuario usuario = usuarioRepository.findByLoginAndApiKey(request.getLogin(), apikeyHeader)
                                        .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

                        // Obtener UserDetails
                        UserDetails userDetails = personaRepository.findByUsuarioLogin(usuario.getId().getLogin())
                                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                        // Generar token
                        String token = jwtService.getToken(userDetails);

                        return AuthResponse.builder()
                                        .token(token)
                                        .message("Inicio de sesión exitoso")
                                        .build();

                } catch (IllegalStateException e) {
                        throw new RuntimeException("Ya existe una sesión activa para este usuario", e);
                } catch (AuthenticationException e) {
                        throw new RuntimeException("Credenciales inválidas", e);
                }
        }

        public void logout(String token) {
                jwtService.invalidateToken(token);
        }

        public AuthResponse registro(RegistroRequest request) {
                log = Logger.getLogger("Pruebassss");
                // Validar si el correo ya existe
                if (personaRepository.findByEmail(request.getEmail()).isPresent()) {
                        return AuthResponse.builder()
                                        .message("El correo ya está registrado.")
                                        .build();
                }
                try {
                        // Crear y configurar la persona
                        Persona persona = Persona.builder()
                                        .pNombre(request.getPNombre())
                                        .sNombre(request.getSNombre())
                                        .pApellido(request.getPApellido())
                                        .sApellido(request.getSApellido())
                                        .email(request.getEmail())
                                        .Ubicacion(request.getUbicacion())
                                        .identificacion(request.getIdentificacion())
                                        .fechanacimiento(request.getFechanacimiento())
                                        .build();

                        // Calcular edad y edad clínica
                        LocalDate today = LocalDate.now();
                        int edad = Period.between(request.getFechanacimiento(), today).getYears();
                        Period period = Period.between(request.getFechanacimiento(), today);
                        String edadClinica = period.getYears() + " años " + period.getMonths() + " meses "
                                        + period.getDays()
                                        + " días";
                        persona.setEdad(edad);
                        persona.setEdadclinica(edadClinica);

                        // Guardar la persona primero para obtener su ID
                        persona = personaRepository.save(persona);

                        // Generar login
                        String login = persona.getPNombre() + persona.getPApellido().charAt(0) + persona.getId();

                        // Crear la clave compuesta
                        UsuarioPK pk = new UsuarioPK(login, persona.getId());

                        // Crear el usuario usando la clave compuesta
                        Usuario usuario = Usuario.builder()
                                        .id(pk)
                                        .persona(persona)
                                        .password(passwordEncoder.encode("1234"))
                                        .apiKey(ApiKeyGenerator.generateApiKey())
                                        .build();
                        // Guardar el usuario
                        usuario = usuarioRepository.save(usuario);
                        log.info("Prueba: " + usuario.toString());

                        UserDetails userDetails = personaRepository.findByUsuarioLogin(usuario.getId().getLogin())
                                        .get();
                        // Generar token JWT
                        String token = jwtService.getToken(userDetails);

                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente
                                                                    // el username)

                        // Enviar a Kafka usando el servicio de auditoría
                        kafkaProducerService.sendAuditLog(username, // usuario
                                        "CREAR", // acción
                                        "Creación de persona con usuario: "
                                                        + usuario.getId().getLogin());

                        return AuthResponse.builder()
                                        .token(token)
                                        .message("Usuario registrado exitosamente. Login: " + usuario.getId().getLogin()
                                                        + ", ApiKey: "
                                                        + usuario.getApiKey())
                                        .build();
                } catch (Exception e) {
                        return AuthResponse.builder()
                                        .message("Error al registrar usuario: " + e.getMessage())
                                        .build();
                }
        }

        // Método privado para generar password inicial
        private String generarPasswordInicial() {
                // Generar un password aleatorio de 8 caracteres
                String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                StringBuilder password = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                        int index = (int) (Math.random() * caracteres.length());
                        password.append(caracteres.charAt(index));
                }
                return password.toString();
        }
}
