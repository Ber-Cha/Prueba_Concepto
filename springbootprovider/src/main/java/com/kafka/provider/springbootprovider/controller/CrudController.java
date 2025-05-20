package com.kafka.provider.springbootprovider.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.provider.springbootprovider.Entity.Persona;
import com.kafka.provider.springbootprovider.Entity.Usuario;
import com.kafka.provider.springbootprovider.annotation.SwaggerDocumentation;
import com.kafka.provider.springbootprovider.component.ScheduledTasks;
import com.kafka.provider.springbootprovider.dto.Mensaje;
import com.kafka.provider.springbootprovider.dto.PasswordUpdateRequest;
import com.kafka.provider.springbootprovider.dto.PersonasDto;
import com.kafka.provider.springbootprovider.service.IPersonaService;
import com.kafka.provider.springbootprovider.service.IUsuarioService;
import com.kafka.provider.springbootprovider.service.KafkaProducerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

// Controlador para la gestión de personas
// Incluye endpoints protegidos por JWT y API Key
@RestController
@RequestMapping("/personas")
public class CrudController {

    @Autowired
    private IPersonaService personaService; // Servicio para operaciones de persona

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CrudController.class);

    @SwaggerDocumentation(summary = "Obtener persona por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPersona(@PathVariable Integer id) {
        Optional<PersonasDto> personaOpt = personaService.get(id);

        if (personaOpt.isEmpty()) {
            Mensaje error = new Mensaje("Persona no encontrada con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        PersonasDto persona = personaOpt.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
        // Enviar a Kafka usando el servicio de auditoría
        kafkaProducerService.sendAuditLog(username, // usuario
                "CONSULTA", // acción
                "Consulta realizada para persona con ID: " + id + " - Nombre: " + persona.getPNombre());

        return ResponseEntity.ok(persona);
    }

    @SwaggerDocumentation(summary = "Obtener todas las personas")
    @CrossOrigin(origins = { "http://localhost" })
    @GetMapping
    public ResponseEntity<?> getAllPersonas() {
        List<PersonasDto> personas = personaService.findAll();
        if (personas.isEmpty()) {
            Mensaje error = new Mensaje("No se encontraron personas");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
        // Enviar a Kafka usando el servicio de auditoría
        kafkaProducerService.sendAuditLog(username, // usuario
                "CONSULTA", // acción
                "Consulta realizada para todas las personas" // detalles
        );

        return ResponseEntity.ok(personas);
    }

    @SwaggerDocumentation(summary = "Eliminar persona por ID")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deletePersona(@PathVariable Integer id) {
        Optional<PersonasDto> personaOpt = personaService.get(id);
        PersonasDto persona = personaOpt.get();

        if (personaOpt.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
            // Enviar a Kafka usando el servicio de auditoría
            kafkaProducerService.sendAuditLog(username, // usuario
                    "ELIMINACION", // acción
                    "Registro de persona eliminado con ID: " + id + " - Nombre: " + persona.getPNombre() // detalles
            );

            personaService.delete(id);
            return ResponseEntity.ok(new Mensaje("Persona eliminada correctamente"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Mensaje("Persona no encontrada con ID: " + id));
    }

    @SwaggerDocumentation(summary = "Buscar personas por identificación")
    @GetMapping("/identificacion")
    public ResponseEntity<?> getByIdentificacion(@RequestParam("Identificacion") Integer identificacion) {
        List<PersonasDto> personas = personaService.findByIdentificacion(identificacion);
        if (personas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No se encontraron personas con identificación: " + identificacion));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
        // Enviar a Kafka usando el servicio de auditoría
        kafkaProducerService.sendAuditLog(username, // usuario
                "CONSULTA", // acción
                "Consulta realizada para usuarios con identificación: " + identificacion// detalles
        );

        return ResponseEntity.ok(personas);
    }

    @SwaggerDocumentation(summary = "Buscar personas por primer nombre")
    @GetMapping("/pNombre/{pNombre}")
    public ResponseEntity<?> getByPNombre(@PathVariable String pNombre) {
        List<PersonasDto> personas = personaService.findByPNombre(pNombre);
        if (personas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No se encontraron personas con nombre: " + pNombre));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
        // Enviar a Kafka usando el servicio de auditoría
        kafkaProducerService.sendAuditLog(username, // usuario
                "CONSULTA", // acción
                "Consulta realizada para usuarios con primer nombre: " + pNombre// detalles
        );
        return ResponseEntity.ok(personas);
    }

    @SwaggerDocumentation(summary = "Buscar personas por primer apellido")
    @GetMapping("/pApellido/{pApellido}")
    public ResponseEntity<?> getByPApellido(@PathVariable String pApellido) {
        List<PersonasDto> personas = personaService.findByPApellido(pApellido);
        if (personas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No se encontraron personas con apellido: " + pApellido));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
        // Enviar a Kafka usando el servicio de auditoría
        kafkaProducerService.sendAuditLog(username, // usuario
                "CONSULTA", // acción
                "Consulta realizada para usuarios con primer apellido: " + pApellido// detalles
        );
        return ResponseEntity.ok(personas);
    }

    @SwaggerDocumentation(summary = "Buscar personas por edad")
    @GetMapping("/edad/{edad}")
    public ResponseEntity<?> getByEdad(@PathVariable Integer edad) {
        List<PersonasDto> personas = personaService.findByEdad(edad);
        if (personas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No se encontraron personas con edad: " + edad));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
        // Enviar a Kafka usando el servicio de auditoría
        kafkaProducerService.sendAuditLog(username, // usuario
                "CONSULTA", // acción
                "Consulta realizada para usuarios con edad: " + edad// detalles
        );
        return ResponseEntity.ok(personas);
    }

    @SwaggerDocumentation(summary = "Actualizar persona")
    @PutMapping("/ActualizarPersona/{id}")
    public ResponseEntity<?> actualizarPersona(@RequestBody PersonasDto personaDto, @PathVariable Integer id) {
        LOGGER.info("Id de la persona :  " + id);
        try {
            personaDto.setId(id); // Establecemos el ID del PathVariable en el DTO
            PersonasDto personaActualizada = personaService.update(personaDto);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
            String usuarioActualizado = usuarioService.getByPersonaId(personaActualizada.getId()).getId().getLogin();
            // Enviar a Kafka usando el servicio de auditoría
            kafkaProducerService.sendAuditLog(username, // usuario
                    "ACTUALIZACIÓN", // acción
                    "Actualización efectutuada a persona con usuario: " + usuarioActualizado);
            return ResponseEntity.ok(personaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("Persona no encontrada con ID: " + id));
        }
    }

    /**
     * Actualiza la contraseña de una persona
     * Requiere autenticación JWT y API Key
     */
    @SwaggerDocumentation(summary = "Actualizar contraseña de persona")
    @PutMapping("/{id}/password")
    public ResponseEntity<?> actualizarPassword(@PathVariable Integer id, @RequestBody PasswordUpdateRequest request) {

        try {
            String newPassword = request.getNewpassword();
            LOGGER.info("Contrasenia: " + newPassword + " Id: " + id);

            boolean resultado = usuarioService.updatePassword(request.getLogin(), id, request.getNewpassword());

            if (!resultado) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se pudo actualizar la contraseña. Verifica los datos.");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Esto devuelve el "subject" del JWT (normalmente el username)
            String usuarioActualizado = usuarioService.getByPersonaId(id).getId().getLogin();
            // Enviar a Kafka usando el servicio de auditoría
            kafkaProducerService.sendAuditLog(username, // usuario
                    "ACTUALIZACIÓN", // acción
                    "Actualización de contraseña efectutuada a persona con usuario: " + usuarioActualizado);
            return ResponseEntity.ok("Contraseña Actualizada Correctamente");
        } catch (Exception ex) {
            LOGGER.error("Error al actualizar contraseña", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar la solicitud: " + ex.getMessage()); // 🔥 Detalles del error
        }
    }
}
