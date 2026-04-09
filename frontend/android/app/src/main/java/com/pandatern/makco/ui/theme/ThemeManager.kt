package com.pandatern.makco.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class AppTheme { DARK, LIGHT }

val LocalThemeManager = compositionLocalOf<ThemeManager> { error("No ThemeManager") }

class ThemeManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("makco_prefs", Context.MODE_PRIVATE)
    private val deviceDark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    var currentTheme by mutableStateOf(AppTheme.DARK) // Default to dark
        private set

    init {
        // Load saved theme or default to dark
        val saved = prefs.getString("app_theme", "DARK")
        currentTheme = try { AppTheme.valueOf(saved!!) } catch (e: Exception) { AppTheme.DARK }
    }

    fun toggle() {
        currentTheme = if (currentTheme == AppTheme.DARK) AppTheme.LIGHT else AppTheme.DARK
        prefs.edit().putString("app_theme", currentTheme.name).apply()
    }

    val isDark get() = currentTheme == AppTheme.DARK

    // Backgrounds
    val bg = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
    val bg2 = if (isDark) Color(0xFF111111) else Color(0xFFEEEEEE)
    val bg3 = if (isDark) Color(0xFF222222) else Color(0xFFDDDDDD)
    val bg4 = if (isDark) Color(0xFF333333) else Color(0xFFCCCCCC)

    // Text colors - increased contrast for visibility
    val t1 = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val t2 = if (isDark) Color(0xFFE0E0E0) else Color(0xFF222222)
    val t3 = if (isDark) Color(0xFFBBBBBB) else Color(0xFF555555)
    val t4 = if (isDark) Color(0xFF888888) else Color(0xFF888888)

    val divider = if (isDark) Color(0xFF333333) else Color(0xFFDDDDDD)

    // Accent colors for actions - subtle accent in neo-brutalist
    val accent = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val accentSubtle = if (isDark) Color(0x33FFFFFF) else Color(0x33000000)
    val success = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val error = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)

    // UI elements
    val glass = if (isDark) Color(0x22FFFFFF) else Color(0x22000000)
    val highlight = if (isDark) Color(0xFF222222) else Color(0xFFEEEEEE)
    val highlightBorder = if (isDark) Color(0xFF444444) else Color(0xFFCCCCCC)
    val outline = if (isDark) Color(0xFF444444) else Color(0xFFBBBBBB)
    
    // Action/Interactive color - primary accent
    val action = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val actionSubtle = if (isDark) Color(0x22FFFFFF) else Color(0x22000000)
}