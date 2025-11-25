package com.udea.innosistemas.innosistemas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;
import com.udea.innosistemas.innosistemas.service.NotificationProducer;

@RestController
@RequestMapping("/project/notification")
public class NotificationController {

    @Autowired
    private NotificationProducer notificationProducer;

    @PostMapping("/send")
    // Endpoint para enviar una notificación por email
    // Recibe un objeto EmailEvent con los campos: to, subject, body
    public ResponseEntity<String> sendNotification(@RequestBody EmailEvent emailEvent) {
        try {
            notificationProducer.sendEmail(emailEvent);
            return ResponseEntity.ok("Notificación enviada exitosamente a " + emailEvent.getTo());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al enviar notificación: " + e.getMessage());
        }
    }
}
