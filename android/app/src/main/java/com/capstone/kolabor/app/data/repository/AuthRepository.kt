package com.capstone.kolabor.app.data.repository

import android.util.Log
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.LoginRequest
import com.capstone.kolabor.app.data.model.LoginResponse
import com.capstone.kolabor.app.data.model.RegisterRequest

class AuthRepository {
    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = RetrofitInstance.api.login(LoginRequest(email, password))
            Log.d("AuthRepo", "Réponse brute: $response")
            response
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erreur réseau", e)
            null
        }
    }

    suspend fun register(request: RegisterRequest): Boolean {
        return try {
            RetrofitInstance.api.register(request)
            true
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erreur inscription", e)
            false
        }
    }
}