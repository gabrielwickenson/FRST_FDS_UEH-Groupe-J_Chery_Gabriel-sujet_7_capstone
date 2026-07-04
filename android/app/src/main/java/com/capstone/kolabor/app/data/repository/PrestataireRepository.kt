package com.capstone.kolabor.app.data.repository

import android.content.Context
import com.capstone.kolabor.app.data.api.RetrofitInstance
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
}