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

    var currentTheme by mutableStateOf(AppTheme.LIGHT) // Default to light
        private set

    init {
        // Load saved theme or default to LIGHT
        val saved = prefs.getString("app_theme", "LIGHT")
        currentTheme = try { AppTheme.valueOf(saved!!) } catch (e: Exception) { AppTheme.LIGHT }
    }

    fun toggle() {
        currentTheme = if (currentTheme == AppTheme.DARK) AppTheme.LIGHT else AppTheme.DARK
        prefs.edit().putString("app_theme", currentTheme.name).apply()
    }

    val isDark get() = currentTheme == AppTheme.DARK

    // Backgrounds (monochrome like Hub)
    val bg = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
    val bg2 = if (isDark) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)
    val bg3 = if (isDark) Color(0xFF2D2D2D) else Color(0xFFEBEBEB)
    val bg4 = if (isDark) Color(0xFF3D3D3D) else Color(0xFFE0E0E0)

    // Text colors - high contrast
    val t1 = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val t2 = if (isDark) Color(0xFFB3B3B3) else Color(0xFF4D4D4D)
    val t3 = if (isDark) Color(0xFF808080) else Color(0xFF737373)
    val t4 = if (isDark) Color(0xFF666666) else Color(0xFF999999)

    val divider = if (isDark) Color(0xFF333333) else Color(0xFFE0E0E0)

    // Action colors - monochrome (like Hub)
    val accent = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val accentSubtle = if (isDark) Color(0x1FFFFFFF) else Color(0x0D000000)
    val success = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val error = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)

    // UI elements
    val glass = if (isDark) Color(0x1FFFFFFF) else Color(0x0D000000)
    val highlight = if (isDark) Color(0xFF262626) else Color(0xFFF0F0F0)
    val highlightBorder = if (isDark) Color(0xFF404040) else Color(0xFFD4D4D4)
    val outline = if (isDark) Color(0xFF4D4D4D) else Color(0xFFCCCCCC)
    
    // Action/Interactive color - monochrome accent
    val action = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val actionSubtle = if (isDark) Color(0x1FFFFFFF) else Color(0x14000000)
}