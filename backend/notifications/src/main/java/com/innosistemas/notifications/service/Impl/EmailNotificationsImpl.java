package com.innosistemas.notifications.service.Impl;

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

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Override
    public void sendEmail(String email, String subject, String content){
        Email from = new Email ("noreply@innosistemas.com");
        Email to = new Email(email);
        Content newContent = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, to, newContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try{
            request.setMethod(Method.POST);
            request.setEndpoint("send/mail");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println(response);
        } catch (IOException ex) {
            throw new RuntimeException("No fue posible enviar el correo.", ex);
        }
    }  
}
