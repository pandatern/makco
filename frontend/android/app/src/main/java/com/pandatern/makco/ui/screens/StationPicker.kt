package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.Station
import com.pandatern.makco.ui.theme.*

private fun getLineColor(code: String): Color {
    return when {
        code.contains("|01") -> MetroBlue
        code.contains("|02") -> MetroGreen
        else -> Color.Gray
    }
}

private fun getLineName(code: String): String {
    return when {
        code.contains("|01") -> "BLUE"
        code.contains("|02") -> "GREEN"
        else -> "OTHER"
    }
}

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

    val grouped = remember(filteredStations) {
        filteredStations.groupBy { getLineName(it.code) }
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

        Text(
            text = "SELECT STATION",
            style = MaterialTheme.typography.labelMedium,
            color = theme.t3,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(theme.bg2)
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search stations...", color = theme.t4) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = theme.t1,
                    unfocusedTextColor = theme.t1,
                    cursorColor = theme.t3
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp)
        ) {
            grouped.forEach { (line, lineStations) ->
                // Line header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(3.dp)
                                .background(
                                    when (line) {
                                        "BLUE" -> MetroBlue
                                        "GREEN" -> MetroGreen
                                        else -> Color.Gray
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$line LINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (line) {
                                "BLUE" -> MetroBlue
                                "GREEN" -> MetroGreen
                                else -> theme.t3
                            }
                        )
                    }
                }

                items(lineStations) { station ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStationSelected(station) }
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(getLineColor(station.code))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = station.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = theme.t1
                            )
                            Text(
                                text = station.code,
                                style = MaterialTheme.typography.labelSmall,
                                color = theme.t4
                            )
                        }
                        Text(text = "→", style = MaterialTheme.typography.titleMedium, color = theme.t4)
                    }
                }
            }
        }
    }
}
