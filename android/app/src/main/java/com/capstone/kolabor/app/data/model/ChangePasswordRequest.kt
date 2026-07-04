package com.capstone.kolabor.app.data.model

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)