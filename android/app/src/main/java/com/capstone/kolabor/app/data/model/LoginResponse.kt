package com.capstone.kolabor.app.data.model

data class LoginResponse(
    val token: String,
    val role: String,
    val id: Long,
    val nom: String
)