package com.innosistemas.notifications.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.innosistemas.notifications.entity.EmailEvent;
import com.innosistemas.notifications.service.NotificationsConsumer;

public class NotificationsConsumerImpl implements NotificationsConsumer{

    @Autowired
    private EmailNotificationsImpl emailNotificationsImpl;

    @KafkaListener(topics = "notifications-topic", groupId = "notification-group")
    public void consumer(EmailEvent email){
        emailNotificationsImpl.sendEmail(email.getTo(), email.getSubject(), email.getBody());
    }
    
}
