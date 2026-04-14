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

    var currentTheme by mutableStateOf(if (deviceDark) AppTheme.DARK else AppTheme.LIGHT)
        private set

    init {
        val saved = prefs.getString("app_theme", "")
        currentTheme = try { 
            if (saved.isNullOrEmpty()) {
                if (deviceDark) AppTheme.DARK else AppTheme.LIGHT
            } else {
                AppTheme.valueOf(saved)
            }
        } catch (e: Exception) { 
            if (deviceDark) AppTheme.DARK else AppTheme.LIGHT 
        }
    }

    fun toggle() {
        currentTheme = if (currentTheme == AppTheme.DARK) AppTheme.LIGHT else AppTheme.DARK
        prefs.edit().putString("app_theme", currentTheme.name).apply()
    }

    val isDark get() = currentTheme == AppTheme.DARK

    // Pure monochrome backgrounds
    val bg = if (isDark) DarkBg else LightBg
    val bg2 = if (isDark) DarkBg2 else LightBg2
    val bg3 = if (isDark) DarkBg3 else LightBg3
    val bg4 = if (isDark) DarkBg4 else LightBg4
    val surface = if (isDark) DarkSurface else LightSurface
    
    // Pure contrast text
    val t1 = if (isDark) Text1Dark else Text1Light
    val t2 = if (isDark) Text2Dark else Text2Light
    val t3 = if (isDark) Text3Dark else Text3Light
    val t4 = if (isDark) Text4Dark else Text4Light
    
    // Dividers & Borders - just contrast
    val divider = if (isDark) DarkBg4 else LightBg4
    val outline = if (isDark) Color(0xFF404040) else Color(0xFFCCCCCC)
    
    // For neo-brutalist: use text color as action
    val action = t1
    val actionSubtle = if (isDark) Color(0x20FFFFFF) else Color(0x20000000)
    
    // Status - just inverted
    val success = t1
    val err = t1
    val warning = t1
    
    // Accent - same as action for brutalist
    val accent = action
    val accentSubtle = actionSubtle
}
