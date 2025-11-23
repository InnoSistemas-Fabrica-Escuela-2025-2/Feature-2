package com.innosistemas.notifications.service;

public interface EmailNotifications {
    
    public void sendEmail(String email, String subject, String content);
}
