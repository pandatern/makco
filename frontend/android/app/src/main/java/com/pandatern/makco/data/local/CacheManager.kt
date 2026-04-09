package com.pandatern.makco.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pandatern.makco.data.model.*

object CacheManager {
    private const val PREFS = "makco_cache"
    private val gson = Gson()

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    // Stations
    fun saveStations(context: Context, stations: List<Station>) {
        prefs(context).edit().putString("stations", gson.toJson(stations)).apply()
        prefs(context).edit().putLong("stations_time", System.currentTimeMillis()).apply()
    }

    fun getStations(context: Context): List<Station>? {
        val json = prefs(context).getString("stations", null) ?: return null
        val time = prefs(context).getLong("stations_time", 0)
        // Cache valid for 24 hours
        if (System.currentTimeMillis() - time > 24 * 60 * 60 * 1000) return null
        val type = object : TypeToken<List<Station>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { null }
    }

    // Routes
    fun saveRoutes(context: Context, routes: List<Route>) {
        prefs(context).edit().putString("routes", gson.toJson(routes)).apply()
    }

    fun getRoutes(context: Context): List<Route>? {
        val json = prefs(context).getString("routes", null) ?: return null
        val type = object : TypeToken<List<Route>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { null }
    }

    // Profile
    fun saveProfile(context: Context, profile: UserProfile) {
        prefs(context).edit().putString("profile", gson.toJson(profile)).apply()
    }

    fun getProfile(context: Context): UserProfile? {
        val json = prefs(context).getString("profile", null) ?: return null
        return try { gson.fromJson(json, UserProfile::class.java) } catch (_: Exception) { null }
    }

    // Last booking
    fun saveLastBooking(context: Context, bookingId: String) {
        prefs(context).edit().putString("last_booking", bookingId).apply()
    }

    fun getLastBooking(context: Context): String? {
        return prefs(context).getString("last_booking", null)
    }

    // Recent stations
    fun addRecentStation(context: Context, station: Station) {
        val recent = getRecentStations(context).toMutableList()
        recent.removeAll { it.code == station.code }
        recent.add(0, station)
        if (recent.size > 5) recent.subList(5, recent.size).clear()
        prefs(context).edit().putString("recent_stations", gson.toJson(recent)).apply()
    }

    fun getRecentStations(context: Context): List<Station> {
        val json = prefs(context).getString("recent_stations", null) ?: return emptyList()
        val type = object : TypeToken<List<Station>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { emptyList() }
    }

    // Booking history
    fun addBookingHistory(context: Context, booking: BookingStatus) {
        val history = getBookingHistory(context).toMutableList()
        history.removeAll { it.bookingId == booking.bookingId }
        history.add(0, booking)
        if (history.size > 20) history.subList(20, history.size).clear()
        prefs(context).edit().putString("booking_history", gson.toJson(history)).apply()
    }

    fun getBookingHistory(context: Context): List<BookingStatus> {
        val json = prefs(context).getString("booking_history", null) ?: return emptyList()
        val type = object : TypeToken<List<BookingStatus>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { emptyList() }
    }

    // Tickets (for API fallback)
    fun saveTickets(context: Context, tickets: List<BookingStatus>) {
        prefs(context).edit().putString("tickets", gson.toJson(tickets)).apply()
        prefs(context).edit().putLong("tickets_time", System.currentTimeMillis()).apply()
    }

    fun getTickets(context: Context): List<BookingStatus>? {
        val json = prefs(context).getString("tickets", null) ?: return null
        val time = prefs(context).getLong("tickets_time", 0)
        if (System.currentTimeMillis() - time > 24 * 60 * 60 * 1000) return null
        val type = object : TypeToken<List<BookingStatus>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { null }
    }

    // Clear all
    fun clearAll(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
