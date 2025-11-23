package com.innosistemas.notifications.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.innosistemas.notifications.entity.EmailEvent;
import com.innosistemas.notifications.service.EmailNotifications;
import com.innosistemas.notifications.service.NotificationService;
import com.innosistemas.notifications.service.NotificationsConsumer;

@Service
public class NotificationsConsumerImpl implements NotificationsConsumer{

    @Autowired
    //Inyectar la dependencia del servicio de notificaciones por correo electrónico
    private EmailNotifications emailNotifications;

    @Autowired
    //Inyectar la dependencia del servicio de notificaciones
    private NotificationService notificationService;

    /*Como usamos la anotación @KafkaListener, este método se ejecutará automáticamente cuando llegue un nuevo mensaje al tópico "notifications-topic"
    Esto usa automaticamente el kafkaListenerContainerFactory definido en KafkaConsumerConfig 
    El topic se refiere al canal de comunicación en Kafka donde se envían y reciben los mensajes
    El groupId identifica de manera única al consumidor dentro de un grupo de consumidores */
    @KafkaListener(topics = "notifications-topic", groupId = "notification-group")
    @Override
    public void consumer(EmailEvent email){
        System.out.print("Sí llegó");
        emailNotifications.sendEmail(email.getTo(), email.getSubject(), email.getBody());
        notificationService.sendNotification(email.getTo(), email.getSubject(), email.getBody());
    }
    
}
