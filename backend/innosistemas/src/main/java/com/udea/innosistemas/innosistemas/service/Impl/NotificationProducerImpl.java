package com.udea.innosistemas.innosistemas.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;
import com.udea.innosistemas.innosistemas.service.NotificationProducer;

@Service 
public class NotificationProducerImpl implements NotificationProducer {

    @Autowired
    // Plantilla de Kafka para enviar mensajes, acepta clave de tipo String (topic) y valor de tipo EmailEvent
    private KafkaTemplate<String, EmailEvent> kafkaTemplate;

    @Override
     //Enviar notificación por email usando un topico de Kafka
    public void sendEmail(EmailEvent email) {
        try{
            // Enviar el evento de email al tópico de Kafka
            kafkaTemplate.send("notifications-topic", email);
            System.out.println("El correo se envió con éxito" + email);
        } catch (Exception e){
            e.printStackTrace();
            throw new UnsupportedOperationException("No es posible:" + e);
        }
    }
    
}
