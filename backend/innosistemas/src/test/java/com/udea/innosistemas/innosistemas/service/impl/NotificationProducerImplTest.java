package com.udea.innosistemas.innosistemas.service.impl;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificationProducerImplTest {
    @Mock
    KafkaTemplate<String, EmailEvent> kafkaTemplate;
    @InjectMocks
    NotificationProducerImpl notificationProducerImpl;

    @Test
    void testSendEmail() {

        EmailEvent email = new EmailEvent("to@test.com", "subject", "body");
        when(kafkaTemplate.send(anyString(), any(EmailEvent.class)))
                .thenReturn(null);
        notificationProducerImpl.sendEmail(email);
        verify(kafkaTemplate, times(1))
                .send("notifications-topic", email);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme