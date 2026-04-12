package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.statusBarsPadding
import androidx.compose.ui.unit.sp
import com.pandatern.makco.ui.theme.LocalThemeManager
import com.pandatern.makco.ui.theme.ThemeManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(themeManager: ThemeManager, onFinished: () -> Unit) {
    val theme = LocalThemeManager.current
    var animStarted by remember { mutableStateOf(false) }

    // Animations
    val pulseScale by animateFloatAsState(
        targetValue = if (animStarted) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "logoScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = tween(600),
        label = "contentAlpha"
    )

    val loadingProgress by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = tween(2000, easing = LinearEasing),
        label = "loadingProgress"
    )

    LaunchedEffect(Unit) {
        delay(200)
        animStarted = true
        delay(2800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
                .background(Brush.radialGradient(colors = listOf(theme.actionSubtle, theme.bg)))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(contentAlpha)
        ) {
            // M Logo with pulse
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale }
                        .background(theme.actionSubtle, RoundedCornerShape(60.dp))
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(theme.action, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "M",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black, fontSize = 48.sp),
                        color = theme.bg,
                        modifier = Modifier.graphicsLayer { scaleX = logoScale; scaleY = logoScale }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "MAKCO",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = theme.t1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "CHENNAI METRO",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 4.sp),
                color = theme.t3
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading bar
            Box(
                modifier = Modifier
                    .width(140.dp)
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
        }
    }
}