package com.udea.innosistemas.innosistemas.config;

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
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;

@Configuration
public class KafkaProducerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    //Crear la fábrica de productores de Kafka
    public ProducerFactory<String, EmailEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>(); // Configuraciones del productor de Kafka
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Dirección del port de Kafka
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(
            config,     // Se retorna el map
            () -> new StringSerializer(),       // Serializador de la clave
            () -> new JsonSerializer<EmailEvent>()  // Serializador del valor
        );
        
    }

    @Bean
    //Crear el template de Kafka
    public KafkaTemplate<String, EmailEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory()); // Usar la fábrica de productores para crear el template de Kafka
    }
}
