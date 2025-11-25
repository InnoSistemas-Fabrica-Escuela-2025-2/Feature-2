package com.udea.innosistemas.innosistemas.config;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

class KafkaProducerConfigTest {
    KafkaProducerConfig kafkaProducerConfig = new KafkaProducerConfig();

    @Test
    void testProducerFactory() {
        ProducerFactory<String, EmailEvent> result = kafkaProducerConfig.producerFactory();
        Assertions.assertNull(result);
    }

    @Test
    void testKafkaTemplate() {
        KafkaTemplate<String, EmailEvent> result = kafkaProducerConfig.kafkaTemplate();
        Assertions.assertEquals(new KafkaTemplate<String, EmailEvent>(null, true, Map.of("configOverrides", "configOverrides")), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme