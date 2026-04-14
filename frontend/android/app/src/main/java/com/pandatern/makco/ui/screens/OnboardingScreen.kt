package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
    val accentColor: Color
)

private val pages = listOf(
    OnboardingPage(
        title = "Metro Made Simple",
        subtitle = "41 Stations",
        description = "Navigate Chennai Metro effortlessly. Every station, every line, right in your pocket.",
        accentColor = LightGreen
    ),
    OnboardingPage(
        title = "Book in Seconds",
        subtitle = "Instant Fares",
        description = "Get instant fare quotes. Choose your journey. Pay seamlessly with UPI.",
        accentColor = LightRed
    ),
    OnboardingPage(
        title = "Skip the Queue",
        subtitle = "QR Tickets",
        description = "Your digital ticket works at every gate. Just scan and ride.",
        accentColor = LightGreen
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
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "MAKCO",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp
                ),
                color = theme.t2
            )
            TextButton(
                onClick = onFinished,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Skip",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.t4
                )
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            val data = pages[page]
            val isSelected = page == pagerState.currentPage
            
            val pageAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.4f,
                animationSpec = tween(400),
                label = "alpha"
            )
            
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale by animateFloatAsState(
                targetValue = 1f - (pageOffset.absoluteValue * 0.1f).coerceIn(0f, 0.1f),
                animationSpec = tween(300),
                label = "scale"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp)
                    .scale(scale)
                    .alpha(pageAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Elegant dot indicator as visual
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            data.accentColor.copy(alpha = 0.12f),
                            RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                data.accentColor,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(56.dp))

                // Title - Apple-like
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        height = 36.sp
                    ),
                    color = theme.t1,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = data.accentColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp
                    ),
                    color = theme.t3,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Page indicators - minimal dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { i ->
                val isSelected = i == pagerState.currentPage
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "width"
                )
                
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(8.dp)
                        .background(
                            if (isSelected) pages[i].accentColor else theme.bg3,
                            RoundedCornerShape(4.dp)
                        )
                )
                if (i < pages.size - 1) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Continue button - minimal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(bottom = 48.dp)
        ) {
            val currentPage = pages[pagerState.currentPage]
            
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = currentPage.accentColor),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "Continue" else "Get Started",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }
        }
    }
}
