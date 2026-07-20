package com.capstone.kolabor.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kolabor.app.MainActivity
import com.kolabor.app.R
import com.capstone.kolabor.app.utils.LocalNotificationManager   // ✅ import ajouté

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "KolaborFCM"
        private const val CHANNEL_ID = "kolabor_notifications"
        private const val CHANNEL_NAME = "Kolabor Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications des réservations, mises à jour et alertes"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "📩 Message reçu de : ${remoteMessage.from}")

        remoteMessage.notification?.let {
            val title = it.title ?: "Kolabor"
            val body = it.body ?: "Nouvelle notification"
            Log.d(TAG, "📨 Titre : $title, Corps : $body")
            try {
                // 1. Afficher la notification système
                sendNotification(title, body)
                // 2. ✅ Sauvegarder localement pour l'app (Bottom Sheet)
                LocalNotificationManager.addNotification(title, body)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erreur affichage notification", e)
            }
        }

        remoteMessage.data.let { data ->
            if (data.isNotEmpty()) {
                Log.d(TAG, "📦 Données reçues : $data")
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🆕 Nouveau token FCM : $token")
        // Envoyer le token au backend (à implémenter)
    }

    private fun sendNotification(title: String?, body: String?) {
        val context = this.applicationContext

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val primaryColor = Color.parseColor("#19355F") // NavyPrimary

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title ?: "Kolabor")
            .setContentText(body ?: "Nouvelle notification")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setSound(defaultSoundUri)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(primaryColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                setSound(defaultSoundUri, AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                )
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 100, 200)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private val notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}