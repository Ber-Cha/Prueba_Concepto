package com.kafka.provider.springbootprovider.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic generateTopic() {
        Map<String, String> configurations = new HashMap<>();
        configurations.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);
        configurations.put(TopicConfig.RETENTION_MS_CONFIG, "86400000"); // 24 horas
        configurations.put(TopicConfig.SEGMENT_BYTES_CONFIG, "1073741824"); // 1GB
        configurations.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, "1000083");

        return TopicBuilder.name("audit-log")
                .partitions(1)
                .replicas(1)
                .configs(configurations)
                .build();
    }
}