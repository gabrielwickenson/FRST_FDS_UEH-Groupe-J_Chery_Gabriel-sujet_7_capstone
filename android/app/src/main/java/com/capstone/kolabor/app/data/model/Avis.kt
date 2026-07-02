package com.capstone.kolabor.app.data.model

data class Avis(
    val id: Long,
    val note: Int,
    val commentaire: String?,
    val date: String,
    val clientNom: String?
)