package com.capstone.kolabor.app.data.model

data class Avis(
    //id de l'avis
    val id: Long,
    val note: Int,
    val commentaire: String?,
    val date: String,
    val clientNom: String?
)