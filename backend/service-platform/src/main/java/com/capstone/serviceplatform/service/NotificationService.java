package com.capstone.serviceplatform.service;

import com.capstone.serviceplatform.entity.Notification;
import com.capstone.serviceplatform.entity.User;
import com.capstone.serviceplatform.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import java.util.Date;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void envoyerNotification(String token, User destinataire, String titre, String corps) {
        // Persister en base
        Notification notif = new Notification();
        notif.setUser(destinataire);
        notif.setMessage(titre + " : " + corps);
        notif.setLu(false);
        notif.setDate(new Date());
        notificationRepository.save(notif);

        // Envoi FCM
        try {
            // Utiliser le nom complet pour éviter le conflit avec l'entité
            com.google.firebase.messaging.Notification firebaseNotif =
                    com.google.firebase.messaging.Notification.builder()
                            .setTitle(titre)
                            .setBody(corps)
                            .build();
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(firebaseNotif)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification envoyée : " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}