package com.capstone.kolabor.app.data.repository

import android.content.Context
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.AvisRequest
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.model.ReservationRequest

class ReservationRepository(private val context: Context) {

    suspend fun getReservationsByClient(clientId: Long): List<Reservation>? {
        return try {
            RetrofitInstance.getApi(context).getReservationsByClient(clientId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createReservation(request: ReservationRequest): Reservation? {
        return try {
            RetrofitInstance.getApi(context).createReservation(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun submitReview(
        reservationId: Long,
        clientId: Long,
        note: Int,
        commentaire: String?
    ): Boolean {
        return try {
            val request = AvisRequest(note, commentaire)
            val response = RetrofitInstance.getApi(context).submitAvis(reservationId, clientId, request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}