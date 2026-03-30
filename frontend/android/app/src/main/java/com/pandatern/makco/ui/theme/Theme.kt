package com.pandatern.makco.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pandatern.makco.ui.theme.*

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.Black,
    primaryContainer = AccentDim,
    onPrimaryContainer = Color.Black,
    secondary = TextSecondary,
    onSecondary = Color.Black,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,
    outline = Divider,
    error = Error,
    onError = Color.White
)

@Composable
fun MakcoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MakcoTypography,
        content = content
    )
}
