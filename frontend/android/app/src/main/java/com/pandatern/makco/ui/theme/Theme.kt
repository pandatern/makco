package com.pandatern.makco.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = LightGreen,
    onPrimary = Black,
    background = DarkBg,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    error = Error,
    onError = White
)

private val LightScheme = lightColorScheme(
    primary = LightRed,
    onPrimary = White,
    background = LightBg,
    onBackground = Black,
    surface = LightSurface,
    onSurface = Black,
    error = Error,
    onError = White
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
