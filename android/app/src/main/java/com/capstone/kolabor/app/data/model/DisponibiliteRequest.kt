package com.capstone.kolabor.app.data.model

data class DisponibiliteRequest(
    val jour: String,      // Ex: "LUNDI"
    val heureDebut: String, // Ex: "08:00:00"
    val heureFin: String    // Ex: "18:00:00"
)