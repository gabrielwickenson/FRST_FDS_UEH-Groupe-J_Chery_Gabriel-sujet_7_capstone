package com.capstone.kolabor.app.data.repository

import android.content.Context
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.Service

class ServiceRepository(private val context: Context) {

    suspend fun getServices(): List<Service>? {
        return try {
            RetrofitInstance.getApi(context).getServices()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}