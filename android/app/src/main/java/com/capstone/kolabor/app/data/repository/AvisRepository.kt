package com.capstone.kolabor.app.data.repository

import android.content.Context
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.Avis

class AvisRepository(private val context: Context) {
    suspend fun getAvisByPrestataire(prestataireId: Long): List<Avis>? {
        return try {
            RetrofitInstance.getApi(context).getAvisByPrestataire(prestataireId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}