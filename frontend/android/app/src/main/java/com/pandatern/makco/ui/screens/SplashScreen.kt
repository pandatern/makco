package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.pandatern.makco.ui.theme.LocalThemeManager
import com.pandatern.makco.ui.theme.ThemeManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(themeManager: ThemeManager, onFinished: () -> Unit) {
    val theme = LocalThemeManager.current

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400),
        label = "alpha"
    )

    val pulseScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            // Neo-brutalist logo block
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .background(theme.action, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "M",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 64.sp
                    ),
                    color = if (theme.isDark) Black else White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Brand name
            Text(
                "MAKCO",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp
                ),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "CHENNAI METRO",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = theme.t3
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading blocks
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { i ->
                    val animAlpha by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, delayMillis = i * 150),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "block$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .alpha(animAlpha)
                            .background(theme.action, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}
