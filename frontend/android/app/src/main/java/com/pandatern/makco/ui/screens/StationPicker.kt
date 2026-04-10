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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.Station
import com.pandatern.makco.ui.theme.*
import androidx.compose.ui.graphics.Color

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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.ic_location), contentDescription = "Back", colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(modifier = Modifier.width(12.dp))
            Text("SELECT STATION", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), color = theme.t1)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search box - styled like profile card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, theme.outline, RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = theme.t1, fontWeight = FontWeight.Medium),
                cursorBrush = SolidColor(theme.t1),
                decorationBox = { innerTextField ->
                    Box {
                        if (searchQuery.isEmpty()) Text("Search stations...", style = MaterialTheme.typography.bodyLarge, color = theme.t4)
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Line filters - styled buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("All" to null, "Blue" to "Blue", "Green" to "Green").forEach { (label, line) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedLine == line) theme.action else theme.bg2)
                        .border(2.dp, if (selectedLine == line) theme.action else theme.outline, RoundedCornerShape(12.dp))
                        .clickable { selectedLine = line }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedLine == line) theme.bg else theme.t2
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Station list
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(filteredStations) { station ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, theme.outline, RoundedCornerShape(12.dp))
                        .clickable { onStationSelected(station) }
                        .background(theme.bg2)
                        .padding(20.dp)
                ) {
                    Text(station.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}