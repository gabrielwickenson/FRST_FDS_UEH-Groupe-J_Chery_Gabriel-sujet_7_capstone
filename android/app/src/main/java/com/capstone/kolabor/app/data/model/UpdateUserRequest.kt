package com.capstone.kolabor.app.data.model

data class UpdateUserRequest(
    val nom: String? = null,
    val telephone: String? = null,
    val adresseParDefaut: String? = null,   // pour Client
    val competences: String? = null,        // pour Prestataire
    val tarifHoraire: Double? = null,       // pour Prestataire
    val zoneIntervention: String? = null    // pour Prestataire
)