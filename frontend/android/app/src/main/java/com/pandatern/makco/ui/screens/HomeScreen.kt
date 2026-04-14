package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.local.SecureCacheManager
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
    val ctx = LocalContext.current
    var recentStations by remember { mutableStateOf(SecureCacheManager.getRecentStations(ctx)) }

    LaunchedEffect(selectedSource, selectedDestination) {
        recentStations = SecureCacheManager.getRecentStations(ctx)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header with gradient accent
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(theme.actionSubtle, theme.bg2)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "M",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = theme.t1
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("MAKCO", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = theme.t1)
                Text("CHENNAI METRO", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t3)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Station selectors - styled like profile cards
        StationSelector(
            label = "FROM",
            icon = R.drawable.ic_location,
            station = selectedSource,
            onClick = { onStationClick(true) },
            theme = theme
        )

        Spacer(modifier = Modifier.height(16.dp))

        StationSelector(
            label = "TO",
            icon = R.drawable.ic_location,
            station = selectedDestination,
            onClick = { onStationClick(false) },
            theme = theme
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search button with action color
        val isReady = selectedSource != null && selectedDestination != null

        Button(
            onClick = onSearchClick,
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.action,
                contentColor = theme.bg,
                disabledContainerColor = theme.bg3,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if (isReady) "SEARCH FARES" else "SELECT STATIONS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent stations section
        if (recentStations.isNotEmpty()) {
            Column {
                Text("RECENT STATIONS", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                Spacer(modifier = Modifier.height(16.dp))

                recentStations.take(3).forEach { station ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, theme.outline, RoundedCornerShape(12.dp))
                            .clickable { onStationClick(true) }
                            .background(theme.bg2)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter = painterResource(R.drawable.ic_location), contentDescription = null, colorFilter = ColorFilter.tint(theme.action), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(station.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = theme.t2)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("→", color = theme.t3)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text("${stations.size} STATIONS AVAILABLE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t4, modifier = Modifier.padding(bottom = 100.dp))
    }
}

@Composable
fun StationSelector(label: String, icon: Int, station: Station?, onClick: () -> Unit, theme: ThemeManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(3.dp, if (station != null) theme.action else theme.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(theme.bg2)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(icon), contentDescription = null, colorFilter = ColorFilter.tint(theme.action), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = station?.name ?: "Select station", style = MaterialTheme.typography.titleMedium.copy(fontWeight = if (station != null) FontWeight.Bold else FontWeight.Normal), color = if (station != null) theme.t1 else theme.t4)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("→", color = theme.t2, style = MaterialTheme.typography.titleLarge)
    }
}