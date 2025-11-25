package com.innosistemas.notifications.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.innosistemas.notifications.service.EmailNotifications;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailNotificationsImpl implements EmailNotifications{

    //Clave API de SendGrid para autenticar el envío de correos
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Override
    //Enviar un correo electrónico usando SendGrid
    public void sendEmail(String email, String subject, String content){
        Email from = new Email ("innosistemas5@gmail.com"); //Correo remitente
        Email to = new Email(email); //Correo destinatario
        Content newContent = new Content("text/plain", content); //Contenido del correo
        Mail mail = new Mail(from, subject, to, newContent);  //Crear el objeto Mail con los detalles del correo

        SendGrid sg = new SendGrid(sendGridApiKey);  //Crear una instancia de SendGrid con la clave API
        Request request = new Request();  //Crear una solicitud para enviar el correo
        try{
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);  //Enviar la solicitud y obtener la respuesta
            System.out.println(response.getStatusCode());
            System.out.println(response);
        } catch (IOException ex) {
            throw new RuntimeException("No fue posible enviar el correo.", ex);
        }
    }  
}
