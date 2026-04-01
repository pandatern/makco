package com.pandatern.makco.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = Dark4,
    onPrimaryContainer = White,
    secondary = Gray3,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = Dark2,
    onSurface = White,
    surfaceVariant = Dark3,
    onSurfaceVariant = Gray4,
    outline = Dark5,
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
