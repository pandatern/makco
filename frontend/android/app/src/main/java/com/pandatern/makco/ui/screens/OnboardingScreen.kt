package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage("41 STATIONS", "BLUE & GREEN LINES", "Book metro tickets across Chennai's entire metro network."),
    OnboardingPage("INSTANT BOOKING", "SEARCH. SELECT. PAY.", "Pick your route, get quotes in seconds, pay via UPI."),
    OnboardingPage("YOUR RIDE", "SIMPLIFIED", "No queues. No cash. Just tap and ride.")
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val theme = LocalThemeManager.current
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    // Animate page transitions
    val animatedPage by animateIntAsState(
        targetValue = pagerState.currentPage,
        animationSpec = tween(300),
        label = "page"
    )

    Column(modifier = Modifier.fillMaxSize().background(theme.bg).padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(80.dp))

        // Animated content
        AnimatedContent(
            targetState = animatedPage,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            label = "onboarding"
        ) { page ->
            val data = pages[page]
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Title with animation
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = theme.t1,
                    modifier = Modifier.graphicsLayer {
                        scaleX = 1f
                        scaleY = 1f
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = theme.t2
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = theme.t3
                )
            }
        }

        // Page indicators with animation
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { i ->
                val isSelected = i == pagerState.currentPage
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = tween(200),
                    label = "indicator_width"
                )
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(4.dp)
                        .background(
                            if (isSelected) theme.t1 else theme.bg4,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
                if (i < pages.size - 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skip button
            TextButton(onClick = onFinished) {
                Text("SKIP", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            }

            // Next/Get Started button
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinished()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.t1,
                    contentColor = theme.bg
                )
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "NEXT" else "GET STARTED",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}