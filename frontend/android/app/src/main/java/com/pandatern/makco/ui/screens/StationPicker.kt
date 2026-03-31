package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.Station
import com.pandatern.makco.ui.theme.*

private fun getLineColor(code: String): androidx.compose.ui.graphics.Color {
    return when {
        code.contains("|01") -> BlueLine
        code.contains("|02") -> GreenLine
        else -> Gray500
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
            .background(Black)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Back
        TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
        }

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text("Search stations...", color = Gray500) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = White,
                unfocusedBorderColor = Gray400,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = White
            ),
            shape = RoundedCornerShape(4.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Station list grouped by line
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            grouped.forEach { (line, lineStations) ->
                // Line header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    when (line) {
                                        "BLUE" -> BlueLine
                                        "GREEN" -> GreenLine
                                        else -> Gray500
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$line LINE",
                            style = MaterialTheme.typography.labelLarge,
                            color = Gray600
                        )
                    }
                }

                items(lineStations) { station ->
                    StationListItem(
                        station = station,
                        lineColor = getLineColor(station.code),
                        onClick = { onStationSelected(station) }
                    )
                }
            }
        }
    }
}

@Composable
fun StationListItem(
    station: Station,
    lineColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Line indicator bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(32.dp)
                .background(lineColor.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = White
                )
            )
            Text(
                text = station.code,
                style = MaterialTheme.typography.labelMedium,
                color = Gray500
            )
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.titleLarge,
            color = Gray400
        )
    }
}
