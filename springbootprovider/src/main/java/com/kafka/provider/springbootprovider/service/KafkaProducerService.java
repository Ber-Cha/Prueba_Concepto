package com.kafka.provider.springbootprovider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaProducerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "audit-log";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendAuditLog(String user, String action, String details) {
        try {
            AuditLogMessage logMessage = new AuditLogMessage(user, action, LocalDateTime.now(), details);
            String message = objectMapper.writeValueAsString(logMessage);
            
            LOGGER.info("Enviando mensaje de auditoría: {}", message);
            kafkaTemplate.send(TOPIC, message);
            LOGGER.info("Mensaje de auditoría enviado exitosamente");
        } catch (Exception e) {
            LOGGER.error("Error al enviar mensaje de auditoría: {}", e.getMessage());
            throw new RuntimeException("Error al enviar mensaje de auditoría", e);
        }
    }

    private static class AuditLogMessage {
        private String user;
        private String action;
        private LocalDateTime timestamp;
        private String details;

        public AuditLogMessage(String user, String action, LocalDateTime timestamp, String details) {
            this.user = user;
            this.action = action;
            this.timestamp = timestamp;
            this.details = details;
        }

        // Getters y Setters
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
    }
} 