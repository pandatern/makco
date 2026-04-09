package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pandatern.makco.ui.theme.ThemeManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(themeManager: ThemeManager, onFinished: () -> Unit) {
    val theme = LocalThemeManager.current

    // Animation states
    var animStarted by remember { mutableStateOf(false) }

    // Pulse animation
    val pulseScale by animateFloatAsState(
        targetValue = if (animStarted) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by animateFloatAsState(
        targetValue = if (animStarted) 0f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Entry animations
    val logoScale by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = tween(800),
        label = "contentAlpha"
    )

    // Loading bar animation
    val loadingProgress by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = tween(2000, easing = LinearEasing),
        label = "loadingProgress"
    )

    LaunchedEffect(Unit) {
        delay(100)
        animStarted = true
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg),
        contentAlignment = Alignment.Center
    ) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.15f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(theme.actionSubtle, theme.bg)
                    )
                )
        )

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(contentAlpha)
        ) {
            // Logo with pulse
            Box(contentAlignment = Alignment.Center) {
                // Pulsing ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = pulseAlpha
                        }
                        .background(theme.action, RoundedCornerShape(60.dp))
                )
                
                // M letter
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(theme.action, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "M",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 48.sp
                        ),
                        color = theme.bg,
                        modifier = Modifier.graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title with animation
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MAKCO",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = theme.t1
                )
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(8.dp)
                        .background(theme.action, RoundedCornerShape(4.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "CHENNAI METRO",
                style = MaterialTheme.typography.labelLarge,
                color = theme.t3,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Animated loading bar
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(4.dp)
                    .background(theme.bg3, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(loadingProgress)
                        .background(theme.action, RoundedCornerShape(2.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.labelSmall,
                color = theme.t4
            )
        }
    }
}