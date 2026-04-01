package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        // From
        StationSlot("FROM", selectedSource, MetroGreen, { onStationClick(true) }, theme)

        // Connector
        Box(modifier = Modifier.padding(start = 16.dp).width(1.dp).height(14.dp).background(theme.divider))

        // To
        StationSlot("TO", selectedDestination, MetroBlue, { onStationClick(false) }, theme)

        Spacer(modifier = Modifier.height(28.dp))

        // Search
        Button(
            onClick = onSearchClick,
            enabled = selectedSource != null && selectedDestination != null,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (theme.isDark) Color(0xFF1A1A1A) else Color(0xFFE0E0E0),
                contentColor = theme.t1,
                disabledContainerColor = theme.bg2,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SEARCH FARES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        // Recent
        if (recentStations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("RECENT", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(8.dp))
            recentStations.take(3).forEach { station ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onStationClick(true) }.padding(vertical = 10.dp),
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
fun StationSlot(label: String, station: Station?, dotColor: Color, onClick: () -> Unit, theme: ThemeManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (theme.isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(10.dp).background(dotColor))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = theme.t4)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                station?.name ?: "Select station",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (station != null) FontWeight.SemiBold else FontWeight.Normal),
                color = if (station != null) theme.t1 else theme.t4
            )
        }
        Text("→", style = MaterialTheme.typography.titleMedium, color = theme.t4)
    }
}
