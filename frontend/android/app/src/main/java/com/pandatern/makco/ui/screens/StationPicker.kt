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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
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
    onBack: () -> Unit,
    isSource: Boolean = true
) {
    val theme = LocalThemeManager.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedLine by remember { mutableStateOf<String?>(null) }

    val filteredStations = remember(stations, searchQuery, selectedLine) {
        stations.filter { station ->
            val matchesSearch = searchQuery.isEmpty() ||
                station.name.contains(searchQuery, ignoreCase = true) ||
                station.code.contains(searchQuery, ignoreCase = true)
            val matchesLine = selectedLine == null || 
                station.code.contains(selectedLine!!, ignoreCase = true) ||
                station.code.lowercase().contains(selectedLine!!.lowercase())
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.bg2)
                    .border(2.dp, theme.outline, RoundedCornerShape(12.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = theme.t1)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                if (isSource) "SELECT SOURCE" else "SELECT DESTINATION", 
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), 
                color = theme.t1
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
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

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("All" to null, "BLUE" to "Blue", "GREEN" to "Green").forEach { (label, line) ->
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedLine == line) theme.action else theme.bg2)
                        .border(3.dp, if (selectedLine == line) theme.action else theme.outline, RoundedCornerShape(12.dp))
                        .clickable { selectedLine = line }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedLine == line) (if (theme.isDark) Black else White) else theme.t2
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            items(filteredStations) { station ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
                        .background(theme.bg2)
                        .clickable { onStationSelected(station) }
                        .padding(20.dp)
                ) {
                    Text(station.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}