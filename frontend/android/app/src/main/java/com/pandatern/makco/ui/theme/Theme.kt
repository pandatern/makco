package com.pandatern.makco.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = Gray300,
    onPrimaryContainer = White,
    secondary = Gray600,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = Gray100,
    onSurface = White,
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,
    outline = Gray400,
    error = Error,
    onError = White
)

@Composable
fun MakcoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MakcoTypography,
        content = content
    )
}
