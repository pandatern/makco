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

    var currentTheme by mutableStateOf(if (deviceDark) AppTheme.DARK else AppTheme.LIGHT) // Follow system by default
        private set

    init {
        // Load saved theme or follow system
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

    // Hybrid monochrome - single base color (black)
    private val monoBase = Color(0xFF000000)
    
    // Import action colors
    private val lightGreen = Color(0xFF8BC34A)
    private val lightRed = Color(0xFFFF8A80)
    
    // Backgrounds - pure monochrome
    val bg = if (isDark) monoBase else Color(0xFFFFFFFF)
    val bg2 = if (isDark) Color(0xFF0D0D0D) else Color(0xFFF8F8F8)
    val bg3 = if (isDark) Color(0xFF1A1A1A) else Color(0xFFF0F0F0)
    val bg4 = if (isDark) Color(0xFF262626) else Color(0xFFE8E8E8)
    
    // Text colors - single base for everything
    val t1 = if (isDark) Color(0xFFFFFFFF) else monoBase
    val t2 = if (isDark) Color(0xFFCCCCCC) else Color(0xFF333333)
    val t3 = if (isDark) Color(0xFF999999) else Color(0xFF666666)
    val t4 = if (isDark) Color(0xFF666666) else Color(0xFF999999)
    
    val divider = if (isDark) Color(0xFF333333) else Color(0xFFE0E0E0)
    
    // Action colors - Hybrid neo-brutalist with light green and light red
    val accent = lightGreen
    val accentSubtle = if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)
    val success = lightGreen
    val error = lightRed
    
    // UI elements
    val glass = accentSubtle
    val highlight = if (isDark) Color(0xFF1A1A1A) else Color(0xFFF5F5F0)
    val highlightBorder = if (isDark) Color(0xFF333333) else Color(0xFFE0E0E0)
    val outline = if (isDark) Color(0xFF404040) else Color(0xFFCDCDCD)
    
    // Action/Interactive - use action colors appropriately
    val action = if (isDark) lightGreen else lightRed  // Dynamic based on theme
    val actionSubtle = accentSubtle
}