package com.capstone.kolabor.app.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Prestataire(
    val id: Long,
    val nom: String,
    val email: String,
    val telephone: String?,
    val photo: String? = null,
    val competences: String?,
    @SerializedName("tarifHoraire") val tarifHoraire: BigDecimal?,
    val zoneIntervention: String?,
    val moyenneNotes: BigDecimal?,
    @SerializedName("nombreAvis") val nombreAvis: Int = 0
)