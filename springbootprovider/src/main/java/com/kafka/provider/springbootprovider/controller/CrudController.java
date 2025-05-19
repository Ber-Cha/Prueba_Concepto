package com.kafka.provider.springbootprovider.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        // Enviar a Kafka usando el servicio de auditoría
        kafkaProducerService.sendAuditLog(
            persona.getPNombre()+" "+persona.getPApellido(), // usuario
            "CONSULTA", // acción
            "Consulta realizada para persona con ID: " + id + " - Nombre: " + persona.getPNombre() // detalles
        );

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
        return ResponseEntity.ok(personas);
    }

    @SwaggerDocumentation(summary = "Eliminar persona por ID")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deletePersona(@PathVariable Integer id) {
        Optional<PersonasDto> personaOpt = personaService.get(id);
        if (personaOpt.isPresent()) {
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
        return ResponseEntity.ok(personas);
    }

    @SwaggerDocumentation(summary = "Actualizar persona")
    @PutMapping("/ActualizarPersona/{id}")
    public ResponseEntity<?> actualizarPersona(@RequestBody PersonasDto personaDto, @PathVariable Integer id) {
        LOGGER.info("Id de la persona :  " + id);
        try {
            personaDto.setId(id); // Establecemos el ID del PathVariable en el DTO
            PersonasDto personaActualizada = personaService.update(personaDto);
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
    public boolean actualizarPassword(@PathVariable Integer id, @RequestBody PasswordUpdateRequest request) {
        String newPassword = request.getNewpassword();
        LOGGER.info("Contrasenia: " + newPassword + " Id: " + id);
        return usuarioService.updatePassword(request.getLogin(), id, newPassword);
    }

}