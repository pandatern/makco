package com.pandatern.makco.data.remote

import com.pandatern.makco.data.model.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MakcoApi {

    // Auth
    @POST("auth")
    suspend fun initiateAuth(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/{authId}/verify")
    suspend fun verifyAuth(
        @Path("authId") authId: String,
        @Body request: VerifyRequest
    ): Response<VerifyResponse>

    // Metro
    @GET("metro/stations")
    suspend fun getStations(
        @Header("token") token: String,
        @Query("city") city: String = "chennai"
    ): Response<List<Station>>

    @GET("metro/routes")
    suspend fun getRoutes(
        @Header("token") token: String,
        @Query("city") city: String = "chennai"
    ): Response<List<Route>>

    // Search
    @POST("metro/search")
    suspend fun searchFare(
        @Header("token") token: String,
        @Query("city") city: String = "chennai",
        @Body request: SearchRequest
    ): Response<SearchResponse>

    @GET("metro/search/{searchId}/quote")
    suspend fun getQuote(
        @Header("token") token: String,
        @Path("searchId") searchId: String,
        @Query("city") city: String = "chennai"
    ): Response<List<Quote>>

    // Booking
    @POST("metro/quote/{quoteId}/confirm")
    suspend fun confirmBooking(
        @Header("token") token: String,
        @Path("quoteId") quoteId: String,
        @Query("city") city: String = "chennai",
        @Query("isMockPayment") isMockPayment: Boolean = false,  // REAL payment
        @Body request: ConfirmRequest = ConfirmRequest()
    ): Response<BookingResponse>

    @GET("booking/{bookingId}/status")
    suspend fun getBookingStatus(
        @Header("token") token: String,
        @Path("bookingId") bookingId: String,
        @Query("city") city: String = "chennai"
    ): Response<BookingStatus>

    @POST("booking/{bookingId}/cancel")
    suspend fun cancelBooking(
        @Header("token") token: String,
        @Path("bookingId") bookingId: String,
        @Query("city") city: String = "chennai",
        @Body request: CancelRequest = CancelRequest()
    ): Response<BookingStatus>

    // User
    @GET("profile")
    suspend fun getProfile(
        @Header("token") token: String
    ): Response<UserProfile>

    // Tickets - use /tickets endpoint
    @GET("tickets")
    suspend fun getTickets(
        @Header("token") token: String,
        @Query("city") city: String = "chennai"
    ): Response<List<BookingStatus>>
}

object ApiClient {
    private const val BASE_URL = "https://api.pandatern.tech/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    val instance: MakcoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MakcoApi::class.java)
    }
}
