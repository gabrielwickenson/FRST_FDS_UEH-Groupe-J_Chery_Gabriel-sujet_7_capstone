package com.capstone.kolabor.app.data.repository

import android.content.Context
import android.util.Log
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
            val response = RetrofitInstance.getApi(context).createReservation(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ReservationRepo", "❌ HTTP ${response.code()}: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ReservationRepo", "❌ Exception", e)
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

    suspend fun getReservationsByPrestataire(prestataireId: Long): List<Reservation>? {
        return try {
            RetrofitInstance.getApi(context).getReservationsByPrestataire(prestataireId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Dans ReservationRepository.kt
    suspend fun updateStatut(reservationId: Long, statut: String, prestataireId: Long): Boolean {
        return try {
            val response = RetrofitInstance.getApi(context).updateStatut(reservationId, statut, prestataireId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}