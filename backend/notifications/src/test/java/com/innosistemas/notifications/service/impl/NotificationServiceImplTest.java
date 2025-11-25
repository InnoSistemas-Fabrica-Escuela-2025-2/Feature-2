package com.innosistemas.notifications.service.impl;

import com.innosistemas.notifications.entity.Notification;
import com.innosistemas.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @Mock
    NotificationRepository notificationRepository;
    @InjectMocks
    NotificationServiceImpl notificationServiceImpl;


    @Test
    void testGetIdByEmail() {
        when(notificationRepository.findIdByEmail(anyString())).thenReturn(Long.valueOf(1));

        Long result = notificationServiceImpl.getIdByEmail("prueba@udea.edu.co");
        Assertions.assertEquals(Long.valueOf(1), result);
    }

    @Test
    void testSendNotification() {
        when(notificationRepository.findIdByEmail(anyString())).thenReturn(Long.valueOf(1));
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        notificationServiceImpl.sendNotification("responsible", "name", "content");
    }

    @Test
    void testDeleteNotification() {
        notificationServiceImpl.deleteNotification(1L);
        verify(notificationRepository).deleteById(1L);
    }
    
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme