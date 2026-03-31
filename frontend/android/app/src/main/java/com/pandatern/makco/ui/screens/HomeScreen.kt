package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black
            )
        )

        Text(
            text = "CHENNAI METRO",
            style = MaterialTheme.typography.labelMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(48.dp))

        // From
        StationRow(
            label = "FROM",
            station = selectedSource,
            lineColor = GreenLine,
            onClick = { onStationClick(true) }
        )

        // Divider
        Box(
            modifier = Modifier
                .padding(start = 20.dp)
                .width(1.dp)
                .height(24.dp)
                .background(Gray400)
        )

        // Swap
        Row(
            modifier = Modifier
                .padding(start = 20.dp)
                .clickable {
                    // swap handled by parent if needed
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Gray500)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "↕",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .padding(start = 20.dp)
                .width(1.dp)
                .height(24.dp)
                .background(Gray400)
        )

        // To
        StationRow(
            label = "TO",
            station = selectedDestination,
            lineColor = BlueLine,
            onClick = { onStationClick(false) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Search
        Button(
            onClick = onSearchClick,
            enabled = selectedSource != null && selectedDestination != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = White,
                contentColor = Black,
                disabledContainerColor = Gray300,
                disabledContentColor = Gray500
            ),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = "SEARCH FARES",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (selectedSource != null && selectedDestination != null) Black else Gray500
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Gray300, RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("STATIONS", style = MaterialTheme.typography.labelMedium, color = Gray500)
                    Text("${stations.size}", style = MaterialTheme.typography.labelLarge, color = White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("LINES", style = MaterialTheme.typography.labelMedium, color = Gray500)
                    Row {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BlueLine)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(GreenLine)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StationRow(
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
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(lineColor)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Gray500
            )
            Text(
                text = station?.name ?: "Select station",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = if (station != null) FontFamily.Default else FontFamily.Monospace,
                    color = if (station != null) White else Gray400
                )
            )
            station?.let {
                Text(
                    text = it.code,
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500
                )
            }
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.headlineMedium,
            color = Gray500
        )
    }
}
