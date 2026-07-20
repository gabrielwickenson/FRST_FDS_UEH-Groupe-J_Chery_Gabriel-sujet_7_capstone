package com.capstone.kolabor.app.utils

import com.capstone.kolabor.app.data.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object LocalNotificationManager {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun addNotification(title: String, body: String) {
        val newNotif = NotificationItem(
            id = System.currentTimeMillis(),
            title = title,
            body = body,
            timestamp = System.currentTimeMillis()
        )
        // Ajouter en tête de liste (les plus récentes en premier)
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun markAsRead(id: Long) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearAll() {
        _notifications.value = emptyList()
    }
}