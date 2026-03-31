package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .background(Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        // Header
        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.displayLarge,
            color = White
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Route selector
        Text(
            text = "TRIP",
            style = MaterialTheme.typography.labelMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(16.dp))

        // From station
        StationSelectorRow(
            label = "FROM",
            station = selectedSource,
            lineColor = GreenLine,
            onClick = { onStationClick(true) }
        )

        // Vertical line connector
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .width(1.dp)
                .height(16.dp)
                .background(Gray300)
        )

        // To station
        StationSelectorRow(
            label = "TO",
            station = selectedDestination,
            lineColor = BlueLine,
            onClick = { onStationClick(false) }
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Search button
        Button(
            onClick = onSearchClick,
            enabled = selectedSource != null && selectedDestination != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = White,
                contentColor = Black,
                disabledContainerColor = Gray200,
                disabledContentColor = Gray400
            )
        ) {
            Text(
                text = "SEARCH FARES",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Line indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(BlueLine)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "BLUE",
                style = MaterialTheme.typography.labelMedium,
                color = Gray500
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(GreenLine)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "GREEN",
                style = MaterialTheme.typography.labelMedium,
                color = Gray500
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${stations.size} STATIONS",
                style = MaterialTheme.typography.labelMedium,
                color = Gray500
            )
        }
    }
}

@Composable
fun StationSelectorRow(
    label: String,
    station: Station?,
    lineColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Line dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(lineColor)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Gray500
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = station?.name ?: "Select station",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (station != null) FontWeight.Medium else FontWeight.Normal
                ),
                color = if (station != null) White else Gray300
            )
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.headlineMedium,
            color = Gray300
        )
    }
}
