package com.innosistemas.notifications.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.innosistemas.notifications.entity.EmailEvent;


@Configuration
public class KafkaConsumerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.properties.security.protocol:SASL_SSL}")
    private String securityProtocol;
    
    @Value("${spring.kafka.properties.sasl.mechanism:PLAIN}")
    private String saslMechanism;
    
    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String saslJaasConfig;
    
    @Bean
    //Crear la fábrica de consumidores de Kafka
    public ConsumerFactory<String, EmailEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();   // Configuraciones del consumidor de Kafka
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // Dirección del port de Kafka
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //Deserializador de la key
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); //Deserializador del valor (EmailEvent)
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        
        // Configuración de seguridad para Confluent Cloud
        config.put("security.protocol", securityProtocol);
        config.put("sasl.mechanism", saslMechanism);
        config.put("sasl.jaas.config", saslJaasConfig);
        
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), 
        new JsonDeserializer<>(EmailEvent.class));
    }

    @Bean
    //Crear el factory de listeners de Kafka
    public ConcurrentKafkaListenerContainerFactory<String, EmailEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailEvent> factory = new ConcurrentKafkaListenerContainerFactory<>(); // Crear una fábrica de listener de Kafka
        factory.setConsumerFactory(consumerFactory()); // Asignar la fábrica de consumidores creada anteriormente
        return factory;
    }
}

