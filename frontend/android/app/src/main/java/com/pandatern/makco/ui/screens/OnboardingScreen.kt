package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
    val icon: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val accentColor: Color
)

private val pages = listOf(
    OnboardingPage(
        icon = "🚇",
        title = "41 STATIONS",
        subtitle = "BLUE & GREEN LINES",
        description = "Every Chennai Metro station at your fingertips. North-South Blue line. East-West Green line.",
        accentColor = LightGreen
    ),
    OnboardingPage(
        icon = "⚡",
        title = "INSTANT BOOKING",
        subtitle = "SEARCH • SELECT • PAY",
        description = "Get fare quotes instantly. Single or return journey. Pay seamless with UPI.",
        accentColor = LightRed
    ),
    OnboardingPage(
        icon = "🎫",
        title = "TAP & RIDE",
        subtitle = "NO QUEUES • NO CASH",
        description = "Skip the counter. QR ticket at every gate. Just scan and board.",
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
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Neo-brutalist logo
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(theme.t1, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "M",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = theme.bg
                )
            }
            
            // Skip - neo style box
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, theme.outline, RoundedCornerShape(8.dp))
                    .clickable { onFinished() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("SKIP", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t3)
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
                targetValue = if (isSelected) 1f else 0.5f,
                animationSpec = tween(200),
                label = "alpha"
            )
            
            val pageScale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.9f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "scale"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .scale(pageScale)
                    .alpha(pageAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Big bold icon block - NEO BRUTALIST
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .shadow(8.dp, RoundedCornerShape(32.dp))
                        .background(data.accentColor, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = data.icon, fontSize = 72.sp)
                }

                Spacer(modifier = Modifier.height(48.dp))

                // BIG BOLD TITLE
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = theme.t1,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle badge
                Box(
                    modifier = Modifier
                        .background(data.accentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = data.subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = data.accentColor
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                    color = theme.t2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Page indicators - NEO BLOCKS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { i ->
                val isSelected = i == pagerState.currentPage
                val width by animateDpAsState(
                    targetValue = if (isSelected) 32.dp else 12.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "width"
                )
                
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(12.dp)
                        .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) pages[i].accentColor else theme.bg3,
                            RoundedCornerShape(6.dp)
                        )
                )
                if (i < pages.size - 1) Spacer(modifier = Modifier.width(12.dp))
            }
        }

        // Bottom CTA - NEO BRUTALIST
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
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
                    .height(64.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = currentPage.accentColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "NEXT" else "GET STARTED →",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = if (theme.isDark) Color.Black else Color.White
                )
            }
        }
    }
}
