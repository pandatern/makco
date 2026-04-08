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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.ui.theme.ThemeManager
import com.pandatern.makco.ui.theme.LocalThemeManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    themeManager: ThemeManager,
    onFinished: () -> Unit
) {
    val theme = LocalThemeManager.current

    var startAnim by remember { mutableStateOf(false) }
    var showDot by remember { mutableStateOf(false) }

    // Logo animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "logoAlpha"
    )

    // Accent dot animation
    val dotAlpha by animateFloatAsState(
        targetValue = if (showDot) 1f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 400),
        label = "dotAlpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(300)
        showDot = true
        delay(1500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated M logo
            Text(
                text = "M",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 72.sp
                ),
                color = theme.t1,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                    }
                    .alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle with accent dot
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MAKCO",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = theme.t1,
                    modifier = Modifier.alpha(logoAlpha)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .padding(start = 4.dp)
                        .background(theme.t1, androidx.compose.foundation.shape.CircleShape)
                        .alpha(dotAlpha)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CHENNAI METRO",
                style = MaterialTheme.typography.labelMedium,
                color = theme.t3,
                modifier = Modifier.alpha(logoAlpha)
            )
        }
    }
}