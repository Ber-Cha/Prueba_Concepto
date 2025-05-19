package com.kafka.consumer.springbootconsumer.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kafka.consumer.springbootconsumer.model.AuditLog;
import com.kafka.consumer.springbootconsumer.repository.AuditLogRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogListener.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public AuditLogListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @KafkaListener(topics = "audit-log", groupId = "audit-group", autoStartup = "true")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            LOGGER.info("==================================================");
            LOGGER.info("Nuevo mensaje recibido en: {}", LocalDateTime.now());
            LOGGER.info("Topic: {}", record.topic());
            LOGGER.info("Partition: {}", record.partition());
            LOGGER.info("Offset: {}", record.offset());
            LOGGER.info("Contenido del mensaje: {}", record.value());

            AuditLogMessage logMessage = objectMapper.readValue(record.value(), AuditLogMessage.class);
            
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(logMessage.getUser());
            auditLog.setAction(logMessage.getAction());
            auditLog.setTimestamp(logMessage.getTimestamp());
            auditLog.setDetails(logMessage.getDetails());
            
            auditLogRepository.save(auditLog);
            LOGGER.info("Log de auditoría guardado exitosamente");
            LOGGER.info("ID del log: {}", auditLog.getId());

            ack.acknowledge();
            LOGGER.info("Mensaje procesado y confirmado");
            LOGGER.info("==================================================");
        } catch (Exception e) {
            LOGGER.error("Error al procesar el mensaje: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar el mensaje", e);
        }
    }

    private static class AuditLogMessage {
        private String user;
        private String action;
        private LocalDateTime timestamp;
        private String details;

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