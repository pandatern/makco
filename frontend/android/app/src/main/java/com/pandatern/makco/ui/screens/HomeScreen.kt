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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.ui.theme.*

@Composable
fun HomeScreen(
    stations: List<Station>,
    onStationClick: (isSource: Boolean) -> Unit,
    onSearchClick: () -> Unit,
    selectedSource: Station?,
    selectedDestination: Station?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // App title
        Text(
            text = "Makco",
            style = MaterialTheme.typography.displayLarge.copy(
                color = Accent,
                fontWeight = FontWeight.Black
            )
        )

        Text(
            text = "Chennai Metro",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Station selector card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Source
                StationSelector(
                    label = "From",
                    station = selectedSource,
                    lineColor = Success,
                    onClick = { onStationClick(true) }
                )

                // Divider with swap icon
                Box(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .width(2.dp)
                        .height(32.dp)
                        .background(Divider)
                )

                // Destination
                StationSelector(
                    label = "To",
                    station = selectedDestination,
                    lineColor = Error,
                    onClick = { onStationClick(false) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search button
        Button(
            onClick = onSearchClick,
            enabled = selectedSource != null && selectedDestination != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                disabledContainerColor = Divider
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Search Fares",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (selectedSource != null && selectedDestination != null)
                        Background
                    else
                        TextMuted,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Stations", "${stations.size}", Accent)
            StatCard("Lines", "2", BlueLine)
            StatCard("Active", "LIVE", Success)
        }
    }
}

@Composable
fun StationSelector(
    label: String,
    station: Station?,
    lineColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(lineColor)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted
            )
            Text(
                text = station?.name ?: "Select station",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (station != null) TextPrimary else TextSecondary
                )
            )
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.headlineMedium,
            color = TextMuted
        )
    }
}

@Composable
fun StatCard(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface2),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}
