package com.pandatern.makco.data.remote

import com.pandatern.makco.data.model.*
import retrofit2.http.*

interface MakcoApi {

    // Auth
    @POST("auth")
    suspend fun initiateAuth(@Body request: AuthRequest): AuthResponse

    @POST("auth/{authId}/verify")
    suspend fun verifyAuth(
        @Path("authId") authId: String,
        @Body request: VerifyRequest
    ): VerifyResponse

    // Metro
    @GET("metro/stations")
    suspend fun getStations(
        @Header("token") token: String,
        @Query("city") city: String = "chennai"
    ): List<Station>

    @GET("metro/routes")
    suspend fun getRoutes(
        @Header("token") token: String,
        @Query("city") city: String = "chennai"
    ): List<Route>

    // Search
    @POST("metro/search")
    suspend fun searchFare(
        @Header("token") token: String,
        @Query("city") city: String = "chennai",
        @Body request: SearchRequest
    ): SearchResponse

    @GET("metro/search/{searchId}/quote")
    suspend fun getQuote(
        @Header("token") token: String,
        @Path("searchId") searchId: String,
        @Query("city") city: String = "chennai"
    ): List<Quote>

    // Booking
    @POST("metro/quote/{quoteId}/confirm")
    suspend fun confirmBooking(
        @Header("token") token: String,
        @Path("quoteId") quoteId: String,
        @Query("city") city: String = "chennai",
        @Body request: ConfirmRequest = ConfirmRequest()
    ): BookingResponse

    @GET("metro/booking/{bookingId}/status")
    suspend fun getBookingStatus(
        @Header("token") token: String,
        @Path("bookingId") bookingId: String,
        @Query("city") city: String = "chennai"
    ): BookingStatus

    // User
    @GET("profile")
    suspend fun getProfile(
        @Header("token") token: String
    ): UserProfile

    @GET("tickets")
    suspend fun getTickets(
        @Header("token") token: String,
        @Query("city") city: String = "chennai"
    ): List<Any>
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"  // Emulator -> host
    // For real device: use your server IP/domain

    val instance: MakcoApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(MakcoApi::class.java)
    }
}
