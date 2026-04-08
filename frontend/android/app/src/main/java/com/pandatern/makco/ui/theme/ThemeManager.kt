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
            else -> if (deviceDark) AppTheme.DARK else AppTheme.DARK // Default to dark
        }
    }

    fun toggle() {
        currentTheme = if (currentTheme == AppTheme.DARK) AppTheme.LIGHT else AppTheme.DARK
        prefs.edit().putString("app_theme", currentTheme.name).apply()
    }

    val isDark get() = currentTheme == AppTheme.DARK

    // Pure black/white - Neo Brutalist
    val bg = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
    val bg2 = if (isDark) Color(0xFF111111) else Color(0xFFEEEEEE)
    val bg3 = if (isDark) Color(0xFF222222) else Color(0xFFDDDDDD)
    val bg4 = if (isDark) Color(0xFF333333) else Color(0xFFCCCCCC)

    // Pure contrast text
    val t1 = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
    val t2 = if (isDark) Color(0xFFCCCCCC) else Color(0xFF333333)
    val t3 = if (isDark) Color(0xFF999999) else Color(0xFF666666)
    val t4 = if (isDark) Color(0xFF666666) else Color(0xFF999999)

    val divider = if (isDark) Color(0xFF333333) else Color(0xFFDDDDDD)

    // Mono - no colors
    val glass = if (isDark) Color(0x33FFFFFF) else Color(0x33000000)
    val glassSelected = if (isDark) Color(0x44FFFFFF) else Color(0x44000000)

    val highlight = if (isDark) Color(0xFF222222) else Color(0xFFEEEEEE)
    val highlightBorder = if (isDark) Color(0xFF444444) else Color(0xFFCCCCCC)
    val highlightText = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)

    val outline = if (isDark) Color(0xFF444444) else Color(0xFFBBBBBB)
    val outlineHighlight = if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
}
