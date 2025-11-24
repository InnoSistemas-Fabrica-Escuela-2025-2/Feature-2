package com.innosistemas.notifications.service;

import java.util.List;

import com.innosistemas.notifications.entity.Notification;

public interface NotificationService {
    public void sendNotification(String responsible, String name, String content);
    public Long getIdByEmail(String email);
    public void deleteNotification(Long id);
    public List<Notification> getById(Long id);
}
