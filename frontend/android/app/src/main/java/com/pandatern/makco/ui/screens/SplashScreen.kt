package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    LaunchedEffect(Unit) {
        delay(1800)
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Neo-brutalist logo block
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(4.dp, theme.t1, RoundedCornerShape(16.dp))
                    .background(theme.bg2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "M",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = theme.t1
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
        }
    }
}
