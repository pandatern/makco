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
    selectedSource: Station?,
    selectedDestination: Station?,
    onStationClick: (isSource: Boolean) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black
            ),
            color = Text1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "CHENNAI METRO",
            style = MaterialTheme.typography.labelMedium,
            color = Text4
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "WHERE TO?",
            style = MaterialTheme.typography.labelMedium,
            color = Text3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // From
        StationSlot(
            label = "FROM",
            station = selectedSource,
            dotColor = MetroGreen,
            onClick = { onStationClick(true) }
        )

        // Connector
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .width(2.dp)
                .height(14.dp)
                .background(Dark5)
        )

        // To
        StationSlot(
            label = "TO",
            station = selectedDestination,
            dotColor = MetroBlue,
            onClick = { onStationClick(false) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSearchClick,
            enabled = selectedSource != null && selectedDestination != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = White,
                contentColor = Black,
                disabledContainerColor = Dark4,
                disabledContentColor = Dark5
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "SEARCH FARES",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Lines info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).background(MetroBlue))
            Spacer(modifier = Modifier.width(6.dp))
            Text("BLUE", style = MaterialTheme.typography.labelSmall, color = Text4)
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(6.dp).background(MetroGreen))
            Spacer(modifier = Modifier.width(6.dp))
            Text("GREEN", style = MaterialTheme.typography.labelSmall, color = Text4)
            Spacer(modifier = Modifier.width(16.dp))
            Text("${stations.size} STATIONS", style = MaterialTheme.typography.labelSmall, color = Text4)
        }
    }
}

@Composable
fun StationSlot(
    label: String,
    station: Station?,
    dotColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Dark3)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(10.dp).background(dotColor)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Text4
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = station?.name ?: "Select station",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (station != null) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (station != null) Text1 else Text4
            )
        }
        Text(text = "→", style = MaterialTheme.typography.titleMedium, color = Text4)
    }
}
