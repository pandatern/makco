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

    var startAnim by remember { mutableStateOf(false) }

    // Pulse animation
    val pulseScale by animateFloatAsState(
        targetValue = if (startAnim) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Logo entrance animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(800),
        label = "logoAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(600, delayMillis = 300),
        label = "textAlpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(theme.actionSubtle, theme.bg)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(logoAlpha + textAlpha)
        ) {
            // Animated M logo with pulse
            Box(contentAlignment = Alignment.Center) {
                // Pulse ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .background(
                            theme.actionSubtle,
                            RoundedCornerShape(50)
                        )
                )
                // M letter
                Text(
                    text = "M",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 72.sp
                    ),
                    color = theme.t1,
                    modifier = Modifier.graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated text
            Column(modifier = Modifier.alpha(textAlpha)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MAKCO",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = theme.t1
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(8.dp)
                            .background(theme.t1, RoundedCornerShape(4.dp))
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

            // Loading bar
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(3.dp)
                    .background(theme.bg3, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .background(theme.t1, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}