package com.innosistemas.notifications.service;

import com.innosistemas.notifications.entity.EmailEvent;

public interface NotificationsConsumer {
    public void consumer(EmailEvent email);
}
