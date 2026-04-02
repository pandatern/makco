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

    var currentTheme by mutableStateOf(loadTheme())
        private set

    private fun loadTheme(): AppTheme {
        val saved = prefs.getString("app_theme", null)
        return when (saved) {
            "LIGHT" -> AppTheme.LIGHT
            "DARK" -> AppTheme.DARK
            else -> if (deviceDark) AppTheme.DARK else AppTheme.LIGHT
        }
    }

    fun toggle() {
        currentTheme = if (currentTheme == AppTheme.DARK) AppTheme.LIGHT else AppTheme.DARK
        prefs.edit().putString("app_theme", currentTheme.name).apply()
    }

    val isDark get() = currentTheme == AppTheme.DARK

    // Pure black for dark, pure white for light
    val bg = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
    val bg2 = if (isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5)
    val bg3 = if (isDark) Color(0xFF111111) else Color(0xFFEBEBEB)
    val bg4 = if (isDark) Color(0xFF1A1A1A) else Color(0xFFDDDDDD)

    // Text colors - MORE WHITE, less grey
    val t1 = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val t2 = if (isDark) Color(0xFFF0F0F0) else Color(0xFF111111)
    val t3 = if (isDark) Color(0xFFD0D0D0) else Color(0xFF333333)
    val t4 = if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)

    val divider = if (isDark) Color(0xFF1A1A1A) else Color(0xFFE0E0E0)

    // Glassmorphism bar color
    val glass = if (isDark) Color(0x22FFFFFF) else Color(0x22000000)
    val glassSelected = if (isDark) Color(0x33FFFFFF) else Color(0x33000000)

    // Highlight colors
    val highlight = if (isDark) Color(0xFF111111) else Color(0xFFE8E8E8)
    val highlightBorder = if (isDark) Color(0xFF222222) else Color(0xFFDDDDDD)
    val highlightText = if (isDark) Color(0xFFE0E0E0) else Color(0xFF1A1A1A)

    // Outline colors
    val outline = if (isDark) Color(0xFF2A2A2A) else Color(0xFFD0D0D0)
    val outlineHighlight = if (isDark) Color(0xFF444444) else Color(0xFFAAAAAA)
}
