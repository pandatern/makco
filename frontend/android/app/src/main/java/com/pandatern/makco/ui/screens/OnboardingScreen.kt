package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Column(modifier = Modifier.fillMaxSize().background(theme.bg)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            val data = pages[page]
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                    color = theme.t1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MetroBlue
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = theme.t3
                )
            }
        }

        // Bottom nav
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { i ->
                    Box(
                        modifier = Modifier
                            .width(if (i == pagerState.currentPage) 24.dp else 8.dp)
                            .height(4.dp)
                            .background(if (i == pagerState.currentPage) theme.t1 else theme.bg4)
                    )
                }
            }

            TextButton(onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinished()
                }
            }) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "NEXT →" else "GET STARTED",
                    style = MaterialTheme.typography.labelLarge,
                    color = theme.t1
                )
            }
        }
    }
}
