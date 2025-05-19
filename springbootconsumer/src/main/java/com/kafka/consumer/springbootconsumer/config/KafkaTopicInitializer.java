package com.kafka.consumer.springbootconsumer.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicInitializer {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic auditLogTopic() {
        return new NewTopic("audit-log", 1, (short) 1);
    }

    @Bean
    public boolean createTopic() {
        try (AdminClient admin = AdminClient.create(kafkaAdmin().getConfigurationProperties())) {
            CreateTopicsResult result = admin.createTopics(Collections.singleton(auditLogTopic()));
            result.all().get();
            return true;
        } catch (Exception e) {
            // Si el tópico ya existe, ignoramos el error
            if (!(e.getCause() instanceof org.apache.kafka.common.errors.TopicExistsException)) {
                throw new RuntimeException("Error al crear el tópico", e);
            }
            return false;
        }
    }
} 