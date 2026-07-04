package com.capstone.kolabor.app.data.repository

import android.content.Context
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.ChangePasswordRequest
import com.capstone.kolabor.app.data.model.UpdateUserRequest
import com.capstone.kolabor.app.data.model.User

class UserRepository(private val context: Context) {
    suspend fun getUserById(userId: Long): User? {
        return try {
            RetrofitInstance.getApi(context).getUserById(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUser(userId: Long, request: UpdateUserRequest): User? {
        return try {
            RetrofitInstance.getApi(context).updateUser(userId, request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun changePassword(userId: Long, oldPassword: String, newPassword: String): Boolean {
        return try {
            val request = ChangePasswordRequest(oldPassword, newPassword)
            val response = RetrofitInstance.getApi(context).changePassword(userId, request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}