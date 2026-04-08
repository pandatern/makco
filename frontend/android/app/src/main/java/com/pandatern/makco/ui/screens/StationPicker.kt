package com.pandatern.makco.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.Station
import com.pandatern.makco.ui.theme.*

@Composable
fun StationPickerScreen(
    stations: List<Station>,
    onStationSelected: (Station) -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredStations = remember(stations, searchQuery) {
        if (searchQuery.isEmpty()) stations
        else stations.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp)) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = theme.t1)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("SELECT STATION", style = MaterialTheme.typography.labelMedium, color = theme.t3, modifier = Modifier.padding(horizontal = 24.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Search with icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .border(2.dp, theme.outline)
                .background(theme.bg2)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_location), contentDescription = null, colorFilter = ColorFilter.tint(theme.t3), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = theme.t1),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.t1)
            ) {
                if (searchQuery.isEmpty()) {
                    Text("Search stations...", color = theme.t4, style = MaterialTheme.typography.bodyLarge)
                }
                it()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Station list with icons
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            items(filteredStations) { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, theme.outline)
                        .clickable { onStationSelected(station) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when {
                        station.code.contains("|01") -> "●"
                        station.code.contains("|02") -> "○"
                        else -> "○"
                    }
                    Text(icon, color = theme.t1, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(station.name, style = MaterialTheme.typography.bodyLarge, color = theme.t1, modifier = Modifier.weight(1f))
                    Text(station.code.split("|").firstOrNull() ?: "", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}