package com.pandatern.makco.ui.screens

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

    LaunchedEffect(selectedSource, selectedDestination) {
        recentStations = com.pandatern.makco.data.local.CacheManager.getRecentStations(ctx)
    }

    // Outline color - opposite of bg
    val outline = if (theme.isDark) Color(0xFF2A2A2A) else Color(0xFFD0D0D0)
    val outlineHighlight = if (theme.isDark) Color(0xFF444444) else Color(0xFFAAAAAA)

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

        // From - with outline
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, if (selectedSource != null) outlineHighlight else outline, RoundedCornerShape(8.dp))
                .clickable { onStationClick(true) }
                .background(theme.bg)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(MetroGreen))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("FROM", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    selectedSource?.name ?: "Select station",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (selectedSource != null) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (selectedSource != null) theme.t1 else theme.t4
                )
            }
            Text("→", style = MaterialTheme.typography.titleMedium, color = theme.t4)
        }

        // Connector
        if (selectedSource != null) {
            Box(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(outline)
            )
        }

        // To - with outline
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, if (selectedDestination != null) outlineHighlight else outline, RoundedCornerShape(8.dp))
                .clickable { onStationClick(false) }
                .background(theme.bg)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(MetroBlue))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("TO", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    selectedDestination?.name ?: "Select station",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (selectedDestination != null) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (selectedDestination != null) theme.t1 else theme.t4
                )
            }
            Text("→", style = MaterialTheme.typography.titleMedium, color = theme.t4)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Search button - with outline
        val isReady = selectedSource != null && selectedDestination != null
        Button(
            onClick = onSearchClick,
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(
                    1.dp,
                    if (isReady) outlineHighlight else outline,
                    RoundedCornerShape(8.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.bg,
                contentColor = if (isReady) theme.t1 else theme.t4,
                disabledContainerColor = theme.bg,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SEARCH FARES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        // Recent - with outline
        if (recentStations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("RECENT", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(8.dp))
            recentStations.take(3).forEach { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, outline, RoundedCornerShape(8.dp))
                        .clickable { onStationClick(true) }
                        .background(theme.bg)
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(MetroBlue))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(station.name, style = MaterialTheme.typography.bodyMedium, color = theme.t3)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
