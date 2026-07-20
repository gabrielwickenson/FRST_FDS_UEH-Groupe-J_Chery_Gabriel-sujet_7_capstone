package com.capstone.kolabor.app.data.model

data class NotificationItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
)