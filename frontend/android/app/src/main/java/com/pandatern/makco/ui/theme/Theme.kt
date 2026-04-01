package com.pandatern.makco.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

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

@Composable
fun MakcoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        typography = MakcoTypography,
        content = content
    )
}
