package com.udea.innosistemas.innosistemas.service;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;

public interface NotificationProducer {
    public void sendEmail(EmailEvent email);
}
