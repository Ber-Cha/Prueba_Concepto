package com.kafka.provider.springbootprovider.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * Configuración del productor de Kafka para la aplicación Spring Boot.
 * Esta clase define la configuración necesaria para establecer la conexión
 * y el comportamiento del productor de mensajes Kafka.
 */
@Configuration
public class KafkaProviderConfig {

    /**
     * Dirección del servidor Kafka obtenida desde la configuración de Spring.
     * Se inyecta automáticamente desde application.properties/yml
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Configura y crea la fábrica de productores de Kafka.
     * Este método establece todas las propiedades necesarias para el productor:
     * - Configuración del servidor bootstrap
     * - Serializadores para claves y valores
     * - Configuración de acuses de recibo
     * - Configuración de reintentos
     * - Habilitación de idempotencia
     * 
     * @return ProducerFactory configurado para producir mensajes String
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Crea y configura el template de Kafka para enviar mensajes.
     * Este bean es el componente principal que se utilizará en la aplicación
     * para enviar mensajes a Kafka.
     * 
     * @return KafkaTemplate configurado para enviar mensajes String
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
