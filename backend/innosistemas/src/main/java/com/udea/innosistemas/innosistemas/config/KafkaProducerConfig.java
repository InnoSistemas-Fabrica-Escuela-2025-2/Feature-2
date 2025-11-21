package com.udea.innosistemas.innosistemas.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;

@Configuration
public class KafkaProducerConfig {
    
    //Eliminamos el warning de deprecated de JsonSerializer
    @SuppressWarnings("removal")
    @Bean
    //Crear la fábrica de productores de Kafka
    public ProducerFactory<String, EmailEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>(); // Configuraciones del productor de Kafka
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Dirección del port de Kafka
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); //Serializador de la key
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); //Serializador del valor (EmailEvent)
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    //Crear el template de Kafka
    public KafkaTemplate<String, EmailEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
