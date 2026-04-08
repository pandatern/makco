package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.ui.theme.LocalThemeManager
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: String,
    val title: String,
    val subtitle: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage("◉", "41 STATIONS", "BLUE & GREEN LINES", "Every station in Chennai at your fingertips. Blue line runs north-south, Green line connects east-west."),
    OnboardingPage("▣", "INSTANT BOOKING", "SEARCH. SELECT. PAY.", "Get fare quotes in seconds. Choose single or return journey. Pay seamlessly with UPI."),
    OnboardingPage("◈", "TAP & RIDE", "NO QUEUES. NO CASH.", "Skip the counter. Your QR ticket works at every gate. Just scan and board.")
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

            // Page content with animations
            val pageOffset by animateFloatAsState(
                targetValue = pagerState.currentPage.toFloat(),
                animationSpec = tween(300),
                label = "pageOffset"
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated icon
                val iconScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "iconScale"
                )

                Text(
                    text = data.icon,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                    color = theme.t1,
                    modifier = Modifier.graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                    color = theme.t2,
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
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                        .background(if (isSelected) theme.t1 else theme.bg4)
                )
                if (i < pages.size - 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Next button
        Button(
            onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinished()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg)
        ) {
            Text(
                text = if (pagerState.currentPage < pages.size - 1) "NEXT" else "GET STARTED",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}