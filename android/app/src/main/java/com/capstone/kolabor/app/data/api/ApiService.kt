package com.capstone.kolabor.app.data.api

import com.capstone.kolabor.app.data.model.Avis
import com.capstone.kolabor.app.data.model.AvisRequest
import com.capstone.kolabor.app.data.model.ChangePasswordRequest
import com.capstone.kolabor.app.data.model.Disponibilite
import com.capstone.kolabor.app.data.model.LoginRequest
import com.capstone.kolabor.app.data.model.LoginResponse
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.model.RegisterRequest
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.model.ReservationRequest
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.model.UpdateUserRequest
import com.capstone.kolabor.app.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("/api/prestataires/{id}/disponibilites")
    suspend fun getDisponibilites(@Path("id") id: Long): List<Disponibilite>

    @GET("/api/reservations/{id}/avis")
    suspend fun getAvisByPrestataire(@Path("id") id: Long): List<Avis>

    @POST("/api/reservations/{id}/avis")
    suspend fun submitAvis(
        @Path("id") id: Long,
        @Query("clientId") clientId: Long,
        @Body request: AvisRequest
    ): Response<Unit>

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): User

    @PUT("/api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UpdateUserRequest
    ): User

    @PUT("/api/users/{id}/password")
    suspend fun changePassword(
        @Path("id") id: Long,
        @Body request: ChangePasswordRequest
    ): Response<Unit>

    @GET("/api/prestataires/{id}/statistiques")
    suspend fun getStatistiques(@Path("id") id: Long): Map<String, Any>
}