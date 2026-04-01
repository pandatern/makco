package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var highlightFrom by remember { mutableStateOf(false) }
    var highlightTo by remember { mutableStateOf(false) }

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

        // From - with highlight on tap
        StationSlot(
            label = "FROM",
            station = selectedSource,
            dotColor = MetroGreen,
            isHighlighted = highlightFrom,
            onClick = {
                highlightFrom = true
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
                    .width(2.dp)
                    .height(14.dp)
                    .background(if (theme.isDark) Color(0xFF333333) else Color(0xFFCCCCCC))
            )
        }

        // To - with highlight on tap
        StationSlot(
            label = "TO",
            station = selectedDestination,
            dotColor = MetroBlue,
            isHighlighted = highlightTo,
            onClick = {
                highlightTo = true
                onStationClick(false)
            },
            theme = theme
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Search button - highlights when ready
        val isReady = selectedSource != null && selectedDestination != null
        Button(
            onClick = onSearchClick,
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .then(
                    if (isReady) Modifier.border(
                        1.dp,
                        if (theme.isDark) Color(0xFF333333) else Color(0xFFCCCCCC),
                        RoundedCornerShape(8.dp)
                    ) else Modifier
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isReady) {
                    if (theme.isDark) Color(0xFF1A1A1A) else Color(0xFFE0E0E0)
                } else {
                    theme.bg
                },
                contentColor = if (isReady) theme.t1 else theme.t4,
                disabledContainerColor = theme.bg,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SEARCH FARES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        // Recent - with highlight on tap
        if (recentStations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("RECENT", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(8.dp))
            recentStations.take(3).forEach { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            highlightFrom = true
                            onStationClick(true)
                        }
                        .background(
                            if (highlightFrom) {
                                if (theme.isDark) Color(0xFF111111) else Color(0xFFE0E0E0)
                            } else {
                                Color.Transparent
                            }
                        )
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
    // Highlight animation
    val bgColor by animateColorAsState(
        targetValue = if (isHighlighted) {
            if (theme.isDark) Color(0xFF111111) else Color(0xFFE8E8E8)
        } else {
            if (theme.isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5)
        },
        label = "highlight"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(bgColor)
            .border(
                width = if (isHighlighted) 1.dp else 0.dp,
                color = if (isHighlighted) {
                    if (theme.isDark) Color(0xFF222222) else Color(0xFFDDDDDD)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot with pulse on highlight
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(dotColor)
        )

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

        // Arrow with highlight
        Text(
            "→",
            style = MaterialTheme.typography.titleMedium,
            color = if (isHighlighted) theme.t2 else theme.t4
        )
    }
}
