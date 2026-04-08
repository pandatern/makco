package com.pandatern.makco.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var selectedLine by remember { mutableStateOf<String?>(null) }

    val filteredStations = remember(stations, searchQuery, selectedLine) {
        stations.filter { station ->
            val matchesSearch = searchQuery.isEmpty() ||
                station.name.contains(searchQuery, ignoreCase = true)
            val matchesLine = selectedLine == null ||
                (selectedLine == "Blue" && station.code.contains("|01")) ||
                (selectedLine == "Green" && station.code.contains("|02"))
            matchesSearch && matchesLine
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.ic_location), contentDescription = "Back", colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(modifier = Modifier.width(12.dp))
            Text("SELECT STATION", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Line filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(selected = selectedLine == null, onClick = { selectedLine = null }, label = "ALL", theme = theme)
            FilterChip(selected = selectedLine == "Blue", onClick = { selectedLine = if (selectedLine == "Blue") null else "Blue" }, label = "BLUE", theme = theme)
            FilterChip(selected = selectedLine == "Green", onClick = { selectedLine = if (selectedLine == "Green") null else "Green" }, label = "GREEN", theme = theme)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, theme.outline)
                .background(theme.bg2)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.ic_location), contentDescription = null, colorFilter = ColorFilter.tint(theme.t3), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = theme.t1),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.t1),
                    decorationBox = { innerTextField ->
                        Box { if (searchQuery.isEmpty()) Text("Search stations...", color = theme.t4, style = MaterialTheme.typography.bodyMedium); innerTextField() }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            items(filteredStations) { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, theme.outline)
                        .clickable { onStationSelected(station) }
                        .background(theme.bg2)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val lineColor = when {
                        station.code.contains("|01") -> theme.t1
                        station.code.contains("|02") -> theme.t2
                        else -> theme.t3
                    }
                    Box(modifier = Modifier.size(12.dp).background(lineColor, RoundedCornerShape(6.dp)))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(station.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = theme.t1)
                        Text(station.code.split("|").firstOrNull() ?: "", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                    }
                    Text("→", color = theme.t3)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FilterChip(selected: Boolean, onClick: () -> Unit, label: String, theme: ThemeManager) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) theme.t1 else theme.bg2)
            .border(1.dp, if (selected) theme.t1 else theme.outline, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal), color = if (selected) theme.bg else theme.t2)
    }
}