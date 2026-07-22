package com.capstone.kolabor.app.utils

fun normalizePhotoUrl(photo: String?): String? {
    if (photo.isNullOrBlank()) return null
    return if (photo.startsWith("http://") || photo.startsWith("https://")) {
        photo
    } else {
        "http://10.0.2.2:8080$photo"
    }
}