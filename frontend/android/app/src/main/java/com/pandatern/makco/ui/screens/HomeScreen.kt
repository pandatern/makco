package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.ui.theme.*

@Composable
fun HomeScreen(
    stations: List<Station>,
    selectedSource: Station?,
    selectedDestination: Station?,
    onStationClick: (isSource: Boolean) -> Unit,
    onSearchClick: () -> Unit
) {
    val theme = LocalThemeManager.current
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var recentStations by remember { mutableStateOf(com.pandatern.makco.data.local.CacheManager.getRecentStations(ctx)) }
    var highlightedSlot by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedSource, selectedDestination) {
        recentStations = com.pandatern.makco.data.local.CacheManager.getRecentStations(ctx)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text("CHENNAI METRO", style = MaterialTheme.typography.labelMedium, color = theme.t4)

        Spacer(modifier = Modifier.height(48.dp))

        Text("WHERE TO?", style = MaterialTheme.typography.labelMedium, color = theme.t3)

        Spacer(modifier = Modifier.height(16.dp))

        // From - with inner glow on tap
        StationSlot(
            label = "FROM",
            station = selectedSource,
            dotColor = MetroGreen,
            isHighlighted = highlightedSlot == "from",
            onClick = {
                highlightedSlot = "from"
                onStationClick(true)
            },
            theme = theme
        )

        // Animated connector
        AnimatedVisibility(
            visible = selectedSource != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .width(1.dp)
                    .height(14.dp)
                    .background(theme.divider)
            )
        }

        // To - with inner glow on tap
        StationSlot(
            label = "TO",
            station = selectedDestination,
            dotColor = MetroBlue,
            isHighlighted = highlightedSlot == "to",
            onClick = {
                highlightedSlot = "to"
                onStationClick(false)
            },
            theme = theme
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Search button - inner glow when ready
        val isReady = selectedSource != null && selectedDestination != null
        val searchBg by animateColorAsState(
            targetValue = if (isReady) {
                if (theme.isDark) Color(0xFF111111) else Color(0xFFE8E8E8)
            } else {
                theme.bg
            },
            label = "searchBg"
        )

        Button(
            onClick = onSearchClick,
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = searchBg,
                contentColor = if (isReady) theme.t1 else theme.t4,
                disabledContainerColor = theme.bg,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SEARCH FARES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        // Recent - with inner glow on tap
        if (recentStations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("RECENT", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(8.dp))
            recentStations.take(3).forEach { station ->
                val isRecentHighlighted = highlightedSlot == "recent-${station.code}"
                val recentBg by animateColorAsState(
                    targetValue = if (isRecentHighlighted) {
                        if (theme.isDark) Color(0xFF111111) else Color(0xFFE8E8E8)
                    } else {
                        Color.Transparent
                    },
                    label = "recentBg"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            highlightedSlot = "recent-${station.code}"
                            onStationClick(true)
                        }
                        .background(recentBg)
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(MetroBlue))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(station.name, style = MaterialTheme.typography.bodyMedium, color = theme.t3)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun StationSlot(
    label: String,
    station: Station?,
    dotColor: Color,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    theme: ThemeManager
) {
    // Inner glow animation
    val bgColor by animateColorAsState(
        targetValue = if (isHighlighted) {
            if (theme.isDark) Color(0xFF0D0D0D) else Color(0xFFF0F0F0)
        } else {
            if (theme.isDark) Color(0xFF050505) else Color(0xFFF8F8F8)
        },
        label = "highlight"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(bgColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot
        Box(modifier = Modifier.size(10.dp).background(dotColor))

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = theme.t4)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                station?.name ?: "Select station",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (station != null) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (station != null) theme.t1 else theme.t4
            )
        }

        // Arrow
        Text("→", style = MaterialTheme.typography.titleMedium, color = theme.t4)
    }
}
