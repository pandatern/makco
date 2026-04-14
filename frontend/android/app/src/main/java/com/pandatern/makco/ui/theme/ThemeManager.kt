package com.pandatern.makco.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

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

    // Premium Backgrounds
    val bg = if (isDark) DarkBg else LightBg
    val bg2 = if (isDark) DarkBg2 else LightBg2
    val bg3 = if (isDark) DarkBg3 else LightBg3
    val bg4 = if (isDark) DarkBg4 else LightBg4
    val surface = if (isDark) DarkSurface else LightSurface
    
    // Premium Text
    val t1 = if (isDark) TextPrimaryDark else TextPrimaryLight
    val t2 = if (isDark) TextSecondaryDark else TextSecondaryLight
    val t3 = if (isDark) TextTertiaryDark else TextTertiaryLight
    val t4 = if (isDark) TextMutedDark else TextMutedLight
    
    // Dividers & Borders
    val divider = if (isDark) Color(0xFF2A2A3A) else Color(0xFFE5E5E8)
    val outline = if (isDark) Color(0xFF3A3A4A) else Color(0xFFD0D0D8)
    
    // Premium Accent - Electric Blue
    val accent = ElectricBlue
    val accentSubtle = if (isDark) Color(0x200066FF) else Color(0x150066FF)
    
    // Action Button - Vibrant Teal (both themes)
    val action = VibrantTeal
    val actionSubtle = if (isDark) Color(0x2000D4AA) else Color(0x1000D4AA)
    
    // Status Colors
    val success = SuccessGreen
    val warning = WarningAmber
    val error = ErrorRed
    val info = InfoBlue
    
    // Glass Effect
    val glass = if (isDark) GlassWhiteDark else GlassWhiteLight
    val glassBorder = if (isDark) GlassBorderDark else GlassBorderLight
    
    // Premium Highlight
    val highlight = if (isDark) DarkBg3 else LightBg3
    val highlightBorder = if (isDark) Color(0xFF2A2A3A) else Color(0xFFE0E0E5)
    
    // Gradient Brushes
    val primaryGradient = Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
    
    val accentGradient = if (isDark) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF00D4AA), Color(0xFF00A8CC))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF0066FF), Color(0xFF00D4AA))
        )
    }
    
    val radialGradient = Brush.radialGradient(
        colors = listOf(accentSubtle, bg)
    )
    
    // Shadow Colors
    val shadowColor = if (isDark) Color(0xFF000000) else Color(0x40000000)
    val elevatedShadow = if (isDark) Color(0x80000000) else Color(0x30000000)
}
