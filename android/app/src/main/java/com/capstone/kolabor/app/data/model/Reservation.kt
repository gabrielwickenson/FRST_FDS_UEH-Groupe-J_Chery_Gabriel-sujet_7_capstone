package com.capstone.kolabor.app.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Reservation(
    val id: Long,
    @SerializedName("dateHeure")
    val dateHeure: String?,
    val adresse: String?,
    val statut: String?,      // EN_ATTENTE, ACCEPTEE, EN_COURS, TERMINEE, ANNULEE
    val montant: BigDecimal?,
    val prestataire: Prestataire?,   // On réutilise le modèle Prestataire
    val service: Service?, // ON peut créer un petit modèle Service (id, nom)
    val client: User?
)