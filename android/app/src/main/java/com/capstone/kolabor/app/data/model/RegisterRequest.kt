package com.capstone.kolabor.app.data.model

data class RegisterRequest(
    val nom: String,
    val email: String,
    val motDePasse: String,
    val role: String, // "CLIENT" ou "PRESTATAIRE"
    val adresseParDefaut: String? = null,
    val competences: String? = null,
    val tarifHoraire: Double? = null,
    val zoneIntervention: String? = null
)