package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.ui.theme.LocalThemeManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(themeManager: com.pandatern.makco.ui.theme.ThemeManager, onFinished: () -> Unit) {
    val theme = LocalThemeManager.current

    var startAnim by remember { mutableStateOf(false) }
    var showPulse by remember { mutableStateOf(false) }

    // Pulse animation for accent
    val pulseScale by animateFloatAsState(
        targetValue = if (showPulse) 1.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val pulseAlpha by animateFloatAsState(
        targetValue = if (showPulse) 0f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Logo entrance
    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "logoScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(600),
        label = "contentAlpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(200)
        showPulse = true
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg),
        contentAlignment = Alignment.Center
    ) {
        // Animated background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.05f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(theme.t1, theme.bg)
                    )
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated M with glow effect
            Box(contentAlignment = Alignment.Center) {
                // Pulse ring behind
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .background(theme.t1.copy(alpha = pulseAlpha), androidx.compose.foundation.shape.CircleShape)
                )
                // Main M
                Text(
                    text = "M",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 80.sp
                    ),
                    color = theme.t1,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                        }
                        .alpha(contentAlpha)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated text
            Column(alpha = contentAlpha) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MAKCO",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = theme.t1
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(10.dp)
                            .background(theme.t1, androidx.compose.foundation.shape.CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CHENNAI METRO",
                    style = MaterialTheme.typography.labelLarge,
                    color = theme.t3,
                    letterSpacing = 4.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(theme.bg3)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .background(theme.t1)
                )
            }
        }
    }
}