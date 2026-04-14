package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.ui.theme.LocalThemeManager
import com.pandatern.makco.ui.theme.LightGreen
import com.pandatern.makco.ui.theme.LightRed
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val gradient: Boolean = false
)

private val pages = listOf(
    OnboardingPage(
        title = "41 STATIONS",
        subtitle = "BLUE & GREEN LINES",
        description = "Every station in Chennai at your fingertips. Blue line runs north-south, Green line connects east-west.",
        gradient = true
    ),
    OnboardingPage(
        title = "INSTANT BOOKING",
        subtitle = "SEARCH. SELECT. PAY.",
        description = "Get fare quotes in seconds. Choose single or return journey. Pay seamlessly with UPI.",
        gradient = false
    ),
    OnboardingPage(
        title = "TAP & RIDE",
        subtitle = "NO QUEUES. NO CASH.",
        description = "Skip the counter. Your QR ticket works at every gate. Just scan and board.",
        gradient = true
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val theme = LocalThemeManager.current
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("MAKCO", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = theme.t1)
            TextButton(onClick = onFinished) {
                Text("SKIP", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Pager with transitions
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            val data = pages[page]
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

            // Animate page content
            val contentAlpha by animateFloatAsState(
                targetValue = if (page == pagerState.currentPage) 1f else 0.5f,
                animationSpec = tween(300),
                label = "contentAlpha"
            )

            val contentScale by animateFloatAsState(
                targetValue = if (page == pagerState.currentPage) 1f else 0.9f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "contentScale"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = contentAlpha
                        scaleX = contentScale
                        scaleY = contentScale
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Accent box with hybrid neo-brutalist colors
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (theme.isDark) LightGreen else LightRed, // Dynamic based on theme
                                    theme.bg2
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (page) {
                            0 -> "M"
                            1 -> "▶"
                            else -> "⚡"
                        },
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp, fontWeight = FontWeight.Black),
                        color = theme.t1
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                    color = theme.t1,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (theme.isDark) theme.t2 else theme.t3, // Adjusted for better contrast
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = theme.t3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { i ->
                val isSelected = i == pagerState.currentPage
                val width by animateDpAsState(
                    targetValue = if (isSelected) 32.dp else 8.dp,
                    tween(200),
                    label = "indicator"
                )
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) theme.t1 else theme.bg4)
                )
                if (i < pages.size - 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Next button with hybrid neo-brutalist colors
        Button(
            onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinished()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (theme.isDark) LightGreen else LightRed
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (pagerState.currentPage < pages.size - 1) "NEXT" else "GET STARTED",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (theme.isDark) theme.bg else theme.bg
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}