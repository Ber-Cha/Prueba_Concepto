package com.kafka.provider.springbootprovider;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class SpringbootproviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootproviderApplication.class, args);
	}

	@Bean
	CommandLineRunner init(KafkaTemplate<String, String> kafkaTemplate) {
		return args -> {
			kafkaTemplate.send("mensaje-prueba", "Hola mundo con Kafka desde Spring Boot");
		};
	}

}
