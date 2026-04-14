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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.pandatern.makco.ui.theme.LocalThemeManager
import com.pandatern.makco.ui.theme.ThemeManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(themeManager: ThemeManager, onFinished: () -> Unit) {
    val theme = LocalThemeManager.current

    // Sophisticated animation sequence
    val logoScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 200),
        label = "logoAlpha"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, delayMillis = 400),
        label = "contentAlpha"
    )

    val shimmerProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    LaunchedEffect(Unit) {
        delay(2500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.6f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            theme.accentSubtle,
                            theme.bg,
                            theme.actionSubtle
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(contentAlpha)
        ) {
            // Premium Logo with glow effect
            Box(contentAlignment = Alignment.Center) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(logoScale * 1.1f)
                        .alpha(0.3f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(theme.action, theme.action.copy(alpha = 0f))
                            ),
                            RoundedCornerShape(80.dp)
                        )
                )

                // Inner glow
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha * 0.5f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(theme.action.copy(alpha = 0.4f), theme.action.copy(alpha = 0f))
                            ),
                            RoundedCornerShape(65.dp)
                        )
                )

                // Main logo container
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                        .background(
                            Brush.linearGradient(
                                colors = if (theme.isDark) {
                                    listOf(theme.action, theme.action.copy(alpha = 0.8f))
                                } else {
                                    listOf(theme.accent, theme.action)
                                }
                            ),
                            RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "M",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 52.sp
                        ),
                        color = if (theme.isDark) theme.bg else theme.bg
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Brand name with shimmer effect
            Text(
                "MAKCO",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp
                ),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "CHENNAI METRO",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 4.sp
                ),
                color = theme.t3
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Premium loading indicator
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .background(theme.bg3, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.7f)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    theme.action,
                                    theme.action.copy(alpha = 0.5f),
                                    theme.action
                                )
                            ),
                            RoundedCornerShape(2.dp)
                        )
                        .graphicsLayer {
                            translationX = shimmerProgress * 200f
                        }
                )
            }
        }

        // Version info at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                "v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = theme.t4.copy(alpha = 0.5f)
            )
        }
    }
}
