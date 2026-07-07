package com.capstone.kolabor.app.data.repository

import android.content.Context
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.AvailabilityRequest
import com.capstone.kolabor.app.data.model.DailyRevenue
import com.capstone.kolabor.app.data.model.Disponibilite
import com.capstone.kolabor.app.data.model.DisponibiliteRequest
import com.capstone.kolabor.app.data.model.Prestataire

class PrestataireRepository(private val context: Context) {

    suspend fun searchPrestataires(
        service: String?,
        noteMin: Double?,
        zone: String?
    ): List<Prestataire>? {
        return try {
            RetrofitInstance.getApi(context).rechercherPrestataires(service, noteMin, zone)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getStatistiques(prestataireId: Long): Map<String, Any>? {
        return try {
            RetrofitInstance.getApi(context).getStatistiques(prestataireId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateAvailability(prestataireId: Long, disponible: Boolean): Boolean {
        return try {
            val response = RetrofitInstance.getApi(context).updateAvailability(prestataireId,
                AvailabilityRequest(disponible)
            )
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getWeeklyRevenue(prestataireId: Long): List<DailyRevenue>? {
        return try {
            RetrofitInstance.getApi(context).getWeeklyRevenue(prestataireId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Disponibilités ---
    suspend fun getDisponibilites(prestataireId: Long): List<Disponibilite>? {
        return try {
            RetrofitInstance.getApi(context).getDisponibilites(prestataireId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addDisponibilite(prestataireId: Long, request: DisponibiliteRequest): Disponibilite? {
        return try {
            RetrofitInstance.getApi(context).addDisponibilite(prestataireId, request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteDisponibilite(disponibiliteId: Long): Boolean {
        return try {
            val response = RetrofitInstance.getApi(context).deleteDisponibilite(disponibiliteId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}