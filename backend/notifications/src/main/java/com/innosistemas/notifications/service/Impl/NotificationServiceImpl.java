package com.innosistemas.notifications.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.innosistemas.notifications.entity.Notification;
import com.innosistemas.notifications.repository.NotificationRepository;
import com.innosistemas.notifications.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    //Inyectar la dependencia del repositorio de notificaciones
    public NotificationRepository notificationRepository;

    @Override
    //Obtener el ID del estudiante por su correo electrónico
    public Long getIdByEmail(String email) {
        try{
            return notificationRepository.findIdByEmail(email);
        } catch(Exception e){
            throw new UnsupportedOperationException("No se pudo encontrar el estudiante: " + e);
        }
    }
        
    @Override
    //Enviar una notificación y guardarla en la base de datos
    public void sendNotification(String responsible, String name, String content) {
        try{
            Long id_student = getIdByEmail(responsible);
            Notification notification = new Notification(null, id_student, name, content);
            notificationRepository.save(notification);
        } catch (Exception e){
            throw new UnsupportedOperationException("No fue posible enviar la notificación: " + e);
        }
        
    }

    
}
