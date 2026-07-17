package com.capstone.kolabor.app.data.repository

import android.content.Context
import android.util.Log
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
            Log.d("PrestataireRepo", "📊 getStatistiques - ID: $prestataireId")
            val result = RetrofitInstance.getApi(context).getStatistiques(prestataireId)
            Log.d("PrestataireRepo", "📊 getStatistiques - succès")
            result
        } catch (e: Exception) {
            Log.e("PrestataireRepo", "❌ getStatistiques", e)
            null
        }
    }

    suspend fun updateAvailability(prestataireId: Long, disponible: Boolean): Boolean {
        return try {
            // La méthode updateAvailability retourne un objet (probablement Unit)
            // Si elle ne lance pas d'exception, on considère que c'est un succès.
            RetrofitInstance.getApi(context).updateAvailability(
                prestataireId,
                AvailabilityRequest(disponible)
            )
            true
        } catch (e: Exception) {
            Log.e("PrestataireRepo", "❌ updateAvailability échoué pour prestataire $prestataireId", e)
            false
        }
    }

    suspend fun getWeeklyRevenue(prestataireId: Long): List<DailyRevenue>? {
        return try {
            RetrofitInstance.getApi(context).getWeeklyRevenue(prestataireId)
        } catch (e: Exception) {
            Log.e("PrestataireRepo", "❌ getWeeklyRevenue échoué pour prestataire $prestataireId", e)
            null
        }
    }

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
            RetrofitInstance.getApi(context).deleteDisponibilite(disponibiliteId)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}