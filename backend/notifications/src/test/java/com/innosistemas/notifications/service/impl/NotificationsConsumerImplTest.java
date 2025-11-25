package com.innosistemas.notifications.service.impl;

import com.innosistemas.notifications.entity.EmailEvent;
import com.innosistemas.notifications.service.EmailNotifications;
import com.innosistemas.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificationsConsumerImplTest {
    @Mock
    EmailNotifications emailNotifications;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    NotificationsConsumerImpl notificationsConsumerImpl;

    @Test
    void testConsumer() {
        notificationsConsumerImpl.consumer(new EmailEvent("to", "subject", "body"));
        verify(emailNotifications).sendEmail(anyString(), anyString(), anyString());
        verify(notificationService).sendNotification(anyString(), anyString(), anyString());
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme