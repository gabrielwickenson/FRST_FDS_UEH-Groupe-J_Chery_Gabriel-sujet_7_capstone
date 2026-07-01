package com.capstone.kolabor.app.data.api

import com.capstone.kolabor.app.data.model.LoginRequest
import com.capstone.kolabor.app.data.model.LoginResponse
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.model.RegisterRequest
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.model.ReservationRequest
import com.capstone.kolabor.app.data.model.Service
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @GET("/api/prestataires/recherche")
    suspend fun rechercherPrestataires(
        @Query("service") service: String?,
        @Query("noteMin") noteMin: Double?,
        @Query("zone") zone: String?
    ): List<Prestataire>

    @GET("/api/reservations/client/{clientId}")
    suspend fun getReservationsByClient(
        @Path("clientId") clientId: Long): List<Reservation>

    @POST("/api/reservations")
    suspend fun createReservation(@Body request: ReservationRequest): Reservation

    @GET("/api/services")
    suspend fun getServices(): List<Service>


}