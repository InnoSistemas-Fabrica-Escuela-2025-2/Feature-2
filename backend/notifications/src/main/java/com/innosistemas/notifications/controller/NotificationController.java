package com.innosistemas.notifications.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innosistemas.notifications.entity.Notification;
import com.innosistemas.notifications.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    // Servicio para manejar notificaciones
    private NotificationService notificationService;

    @Operation(
    summary = "Listar todas las notificaciones por id"
    )
    @GetMapping("/listAll/{id}")
    public ResponseEntity<List<Notification>> listAllNotifications(@PathVariable Long id) {
        List<Notification> notifications = notificationService.getById(id);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
    summary = "Eliminar una notificación por su id"
    )
    @PutMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

}