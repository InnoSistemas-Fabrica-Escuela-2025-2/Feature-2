package com.innosistemas.notifications.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.innosistemas.notifications.entity.EmailEvent;


@Configuration
public class KafkaConsumerConfig {
    
    @Bean
    //Crear la fábrica de consumidores de Kafka
    public ConsumerFactory<String, EmailEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();   // Configuraciones del consumidor de Kafka
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Dirección del port de Kafka
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //Deserializador de la key
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); //Deserializador del valor (EmailEvent)
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), 
        new JsonDeserializer<>(EmailEvent.class));
    }

    @Bean
    //Crear el factory de listeners de Kafka
    public ConcurrentKafkaListenerContainerFactory<String, EmailEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailEvent> factory = new ConcurrentKafkaListenerContainerFactory<>(); // Crear una fábrica de listener de Kafka
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

