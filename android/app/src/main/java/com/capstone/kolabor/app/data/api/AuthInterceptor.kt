package com.capstone.kolabor.app.data.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import com.capstone.kolabor.app.utils.TokenManager
import kotlinx.coroutines.runBlocking

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenManager = TokenManager(context)
        val token = runBlocking { tokenManager.getToken() }
        println("🔑 Token from interceptor: $token")
        val request = chain.request().newBuilder()
        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(request.build())
    }
}