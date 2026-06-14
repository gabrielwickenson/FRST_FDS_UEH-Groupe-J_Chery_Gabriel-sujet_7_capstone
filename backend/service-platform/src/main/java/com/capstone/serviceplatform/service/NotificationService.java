package com.capstone.serviceplatform.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void envoyerNotification(String token, String titre, String corps) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(titre)
                    .setBody(corps)
                    .build();
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification envoyée : " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
