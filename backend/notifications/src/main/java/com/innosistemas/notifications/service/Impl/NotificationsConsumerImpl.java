package com.innosistemas.notifications.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.innosistemas.notifications.entity.EmailEvent;
import com.innosistemas.notifications.service.NotificationsConsumer;

public class NotificationsConsumerImpl implements NotificationsConsumer{

    @Autowired
    private EmailNotificationsImpl emailNotificationsImpl;

    //Como usamos la anotación @KafkaListener, este método se ejecutará automáticamente cuando llegue un nuevo mensaje al tópico "notifications-topic"
    //Esto usa automaticamente el kafkaListenerContainerFactory definido en KafkaConsumerConfig 
    //El topic se refiere al canal de comunicación en Kafka donde se envían y reciben los mensajes
    //El groupId identifica de manera única al consumidor dentro de un grupo de consumidores
    @KafkaListener(topics = "notifications-topic", groupId = "notification-group")
    public void consumer(EmailEvent email){
        emailNotificationsImpl.sendEmail(email.getTo(), email.getSubject(), email.getBody());
    }
    
}
