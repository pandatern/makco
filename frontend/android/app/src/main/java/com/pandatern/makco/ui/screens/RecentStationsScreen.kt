package com.pandatern.makco.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.Station
import com.pandatern.makco.data.local.CacheManager
import com.pandatern.makco.ui.theme.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun RecentStationsScreen(
    onStationClick: (Station) -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    val context = LocalContext.current
    var recentStations by remember { mutableStateOf<List<Station>>(emptyList()) }

    LaunchedEffect(Unit) {
        recentStations = CacheManager.getRecentStations(context) ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Text("←", style = MaterialTheme.typography.headlineMedium, color = theme.t1)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "RECENT STATIONS",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = theme.t1
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (recentStations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(theme.t4),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("NO RECENT STATIONS", style = MaterialTheme.typography.titleMedium, color = theme.t3)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Your recently used stations will appear here", style = MaterialTheme.typography.bodySmall, color = theme.t4)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                items(recentStations) { station ->
                    StationListItem(
                        station = station,
                        theme = theme,
                        onClick = { onStationClick(station) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun StationListItem(
    station: Station,
    theme: ThemeManager,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, theme.outline)
            .clickable(onClick = onClick)
            .background(theme.bg2)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_location),
            contentDescription = null,
            colorFilter = ColorFilter.tint(theme.t2),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                station.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = theme.t1
            )
            station.code.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = theme.t2
                )
            }
        }
        Text("→", style = MaterialTheme.typography.titleMedium, color = theme.t3)
    }
}