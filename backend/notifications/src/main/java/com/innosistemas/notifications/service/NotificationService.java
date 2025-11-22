package com.innosistemas.notifications.service;

public interface NotificationService {
    public void sendNotification(String responsible, String name, String content);
    public Long getIdByEmail(String email);
}
