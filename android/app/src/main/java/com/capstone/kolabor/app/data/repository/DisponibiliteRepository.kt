package com.capstone.kolabor.app.data.repository

import android.content.Context
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.Disponibilite

class DisponibiliteRepository(private val context: Context) {
    suspend fun getDisponibilitesByPrestataire(prestataireId: Long): List<Disponibilite>? {
        return try {
            RetrofitInstance.getApi(context).getDisponibilites(prestataireId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}