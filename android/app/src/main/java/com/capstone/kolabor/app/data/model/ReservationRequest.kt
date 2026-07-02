package com.capstone.kolabor.app.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ReservationRequest(
    @SerializedName("clientId")
    val clientId: Long,
    @SerializedName("prestataireId")
    val prestataireId: Long,
    @SerializedName("serviceId")
    val serviceId: Long,
    @SerializedName("dateHeure")
    val dateHeure: String,
    @SerializedName("adresse")
    val adresse: String,
    @SerializedName("montant")
    val montant: Double
)