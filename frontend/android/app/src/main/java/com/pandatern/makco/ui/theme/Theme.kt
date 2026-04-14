package com.pandatern.makco.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    background = DarkBg,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    error = Color.White,
    onError = Color.Black
)

private val LightScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = LightBg,
    onBackground = Color.Black,
    surface = LightSurface,
    onSurface = Color.Black,
    error = Color.Black,
    onError = Color.White
)

@Composable
fun MakcoTheme(
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDark) DarkScheme else LightScheme,
        typography = MakcoTypography,
        content = content
    )
}
