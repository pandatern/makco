package com.pandatern.makco.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    background = Black,
    onBackground = White,
    surface = Dark2,
    onSurface = White,
    error = Error,
    onError = White
)

private val LightScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,
    background = White,
    onBackground = Black,
    surface = Light1,
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
