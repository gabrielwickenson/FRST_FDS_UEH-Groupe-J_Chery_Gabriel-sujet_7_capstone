package com.capstone.kolabor.app.data.model

data class User(
    val id: Long,
    val nom: String,
    val email: String,
    val telephone: String?,
    val role: String,
    val photo: String?
)