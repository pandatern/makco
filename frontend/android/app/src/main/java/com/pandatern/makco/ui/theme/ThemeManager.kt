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

    val bg get() = if (isDark) Color.Black else Color(0xFFF5F5F5)
    val bg2 get() = if (isDark) Color(0xFF141414) else Color(0xFFEBEBEB)
    val bg3 get() = if (isDark) Color(0xFF1C1C1C) else Color(0xFFE0E0E0)
    val bg4 get() = if (isDark) Color(0xFF282828) else Color(0xFFD0D0D0)

    val t1 get() = if (isDark) Color.White else Color.Black
    val t2 get() = if (isDark) Color(0xFFE8E8E8) else Color(0xFF1A1A1A)
    val t3 get() = if (isDark) Color(0xFFD0D0D0) else Color(0xFF333333)
    val t4 get() = if (isDark) Color(0xFFB0B0B0) else Color(0xFF666666)

    val divider get() = if (isDark) Color(0xFF282828) else Color(0xFFD0D0D0)
}
