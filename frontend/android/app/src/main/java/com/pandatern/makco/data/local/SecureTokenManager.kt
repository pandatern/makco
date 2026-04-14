package com.pandatern.makco.data.local

import android.content.Context
import android.provider.Settings
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.File

data class AccountData(
    @SerializedName("phone") val phone: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("token") val token: String,
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @SerializedName("lastLoginAt") val lastLoginAt: Long = System.currentTimeMillis()
)

object SecureTokenManager {
    private const val ACCOUNTS_FILE = "accounts.json"
    private val gson = Gson()

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "default"
    }

    private fun getAccountsFile(context: Context): File {
        return File(context.filesDir, ACCOUNTS_FILE)
    }

    fun getCurrentAccount(context: Context): AccountData? {
        return try {
            val file = getAccountsFile(context)
            if (!file.exists()) return null
            val json = decrypt(file.readText(), getDeviceId(context))
            val type = object : TypeToken<AccountData>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveAccount(context: Context, phone: String, userId: String, token: String) {
        try {
            val account = AccountData(
                phone = phone,
                userId = userId,
                token = token,
                lastLoginAt = System.currentTimeMillis()
            )
            val json = gson.toJson(account)
            val encrypted = encrypt(json, getDeviceId(context))
            getAccountsFile(context).writeText(encrypted)
        } catch (e: Exception) {
            // Ignore write errors
        }
    }

    fun logout(context: Context) {
        try {
            getAccountsFile(context).delete()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun hasAccount(context: Context): Boolean {
        return getCurrentAccount(context) != null
    }

    private fun encrypt(data: String, key: String): String {
        val keyBytes = key.toByteArray()
        val dataBytes = data.toByteArray()
        val result = ByteArray(dataBytes.size)
        for (i in dataBytes.indices) {
            result[i] = (dataBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
        return android.util.Base64.encodeToString(result, android.util.Base64.NO_WRAP)
    }

    private fun decrypt(data: String, key: String): String {
        val keyBytes = key.toByteArray()
        val dataBytes = android.util.Base64.decode(data, android.util.Base64.NO_WRAP)
        val result = ByteArray(dataBytes.size)
        for (i in dataBytes.indices) {
            result[i] = (dataBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
        return String(result)
    }
}

object SecureCacheManager {
    private const val CACHE_FILE = "cache.dat"
    private val gson = Gson()

    fun saveStations(context: Context, stations: List<com.pandatern.makco.data.model.Station>) {
        try {
            val data = mapOf(
                "stations" to gson.toJson(stations),
                "stations_time" to System.currentTimeMillis()
            )
            saveCache(context, data)
        } catch (e: Exception) { /* Ignore */ }
    }

    fun getStations(context: Context): List<com.pandatern.makco.data.model.Station>? {
        return try {
            val cache = loadCache(context) ?: return null
            val stationsJson = cache["stations"] as? String ?: return null
            val time = (cache["stations_time"] as? Number)?.toLong() ?: 0L
            if (System.currentTimeMillis() - time > 24 * 60 * 60 * 1000) return null
            val type = object : TypeToken<List<com.pandatern.makco.data.model.Station>>() {}.type
            gson.fromJson(stationsJson, type)
        } catch (e: Exception) { null }
    }

    fun saveRecentStations(context: Context, stations: List<com.pandatern.makco.data.model.Station>) {
        try {
            val cache = loadCache(context)?.toMutableMap() ?: mutableMapOf()
            cache["recent_stations"] = gson.toJson(stations)
            saveCache(context, cache)
        } catch (e: Exception) { /* Ignore */ }
    }

    fun getRecentStations(context: Context): List<com.pandatern.makco.data.model.Station> {
        return try {
            val cache = loadCache(context) ?: return emptyList()
            val json = cache["recent_stations"] as? String ?: return emptyList()
            val type = object : TypeToken<List<com.pandatern.makco.data.model.Station>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) { emptyList() }
    }

    fun addRecentStation(context: Context, station: com.pandatern.makco.data.model.Station) {
        try {
            val recent = getRecentStations(context).toMutableList()
            recent.removeAll { it.code == station.code }
            recent.add(0, station)
            if (recent.size > 5) recent.subList(5, recent.size).clear()
            saveRecentStations(context, recent)
        } catch (e: Exception) { /* Ignore */ }
    }

    fun addBookingHistory(context: Context, booking: com.pandatern.makco.data.model.BookingStatus) {
        try {
            val history = getBookingHistory(context).toMutableList()
            history.removeAll { it.bookingId == booking.bookingId }
            history.add(0, booking)
            if (history.size > 20) history.subList(20, history.size).clear()
            saveBookingHistory(context, history)
        } catch (e: Exception) { /* Ignore */ }
    }

    fun saveBookingHistory(context: Context, bookings: List<com.pandatern.makco.data.model.BookingStatus>) {
        try {
            val cache = loadCache(context)?.toMutableMap() ?: mutableMapOf()
            cache["booking_history"] = gson.toJson(bookings)
            saveCache(context, cache)
        } catch (e: Exception) { /* Ignore */ }
    }

    fun getBookingHistory(context: Context): List<com.pandatern.makco.data.model.BookingStatus> {
        return try {
            val cache = loadCache(context) ?: return emptyList()
            val json = cache["booking_history"] as? String ?: return emptyList()
            val type = object : TypeToken<List<com.pandatern.makco.data.model.BookingStatus>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) { emptyList() }
    }

    fun saveTickets(context: Context, tickets: List<com.pandatern.makco.data.model.BookingStatus>) {
        try {
            val cache = loadCache(context)?.toMutableMap() ?: mutableMapOf()
            cache["tickets"] = gson.toJson(tickets)
            cache["tickets_time"] = System.currentTimeMillis()
            saveCache(context, cache)
        } catch (e: Exception) { /* Ignore */ }
    }

    fun getTickets(context: Context): List<com.pandatern.makco.data.model.BookingStatus>? {
        return try {
            val cache = loadCache(context) ?: return null
            val ticketsJson = cache["tickets"] as? String ?: return null
            val time = (cache["tickets_time"] as? Number)?.toLong() ?: 0L
            if (System.currentTimeMillis() - time > 24 * 60 * 60 * 1000) return null
            val type = object : TypeToken<List<com.pandatern.makco.data.model.BookingStatus>>() {}.type
            gson.fromJson(ticketsJson, type)
        } catch (e: Exception) { null }
    }

    fun clearAll(context: Context) {
        try {
            File(context.filesDir, CACHE_FILE).delete()
        } catch (e: Exception) { /* Ignore */ }
    }

    private fun getCacheFile(context: Context): File {
        return File(context.filesDir, CACHE_FILE)
    }

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "default"
    }

    private fun saveCache(context: Context, data: Map<String, Any>) {
        try {
            val json = gson.toJson(data)
            val encrypted = encrypt(json, getDeviceId(context))
            getCacheFile(context).writeText(encrypted)
        } catch (e: Exception) { /* Ignore */ }
    }

    private fun loadCache(context: Context): Map<String, Any>? {
        return try {
            val file = getCacheFile(context)
            if (!file.exists()) return null
            val json = decrypt(file.readText(), getDeviceId(context))
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) { null }
    }

    private fun encrypt(data: String, key: String): String {
        val keyBytes = key.toByteArray()
        val dataBytes = data.toByteArray()
        val result = ByteArray(dataBytes.size)
        for (i in dataBytes.indices) {
            result[i] = (dataBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
        return android.util.Base64.encodeToString(result, android.util.Base64.NO_WRAP)
    }

    private fun decrypt(data: String, key: String): String {
        val keyBytes = key.toByteArray()
        val dataBytes = android.util.Base64.decode(data, android.util.Base64.NO_WRAP)
        val result = ByteArray(dataBytes.size)
        for (i in dataBytes.indices) {
            result[i] = (dataBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
        return String(result)
    }
}
