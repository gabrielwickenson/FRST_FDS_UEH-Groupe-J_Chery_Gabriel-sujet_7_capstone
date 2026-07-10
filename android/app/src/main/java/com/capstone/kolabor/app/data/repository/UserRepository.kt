package com.capstone.kolabor.app.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.capstone.kolabor.app.data.api.RetrofitInstance
import com.capstone.kolabor.app.data.model.ChangePasswordRequest
import com.capstone.kolabor.app.data.model.UpdateUserRequest
import com.capstone.kolabor.app.data.model.User
import okhttp3.MultipartBody
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody

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

    suspend fun uploadPhoto(userId: Long, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                Log.e("UserRepo", "Impossible d'ouvrir l'URI : $imageUri")
                return null
            }
            val tempFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
            try {
                inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val mediaType = (context.contentResolver.getType(imageUri) ?: "image/jpeg").toMediaType()
                val requestBody = tempFile.asRequestBody(mediaType)
                val multipartBody = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

                val api = RetrofitInstance.getApi(context)
                val postResponse = api.uploadPhoto(userId, multipartBody)
                val response = if (!postResponse.isSuccessful) {
                    // Certains backends acceptent POST à la création, mais exigent PUT au remplacement.
                    api.updatePhoto(userId, multipartBody)
                } else {
                    postResponse
                }

                if (response.isSuccessful) {
                    val result = response.body().orEmpty()
                    result["photoUrl"]
                        ?: result["photo"]
                        ?: result["url"]
                        ?: result["path"]
                        ?: getUserById(userId)?.photo
                } else {
                    null
                }
            } finally {
                tempFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun getRealPathFromUri(uri: Uri): String {
        val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        } ?: return uri.path ?: ""
    }
}