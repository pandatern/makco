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
    onProfileClick: () -> Unit,
    onTicketsClick: () -> Unit,
    selectedSource: Station?,
    selectedDestination: Station?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MAKCO",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = White
                )
            }

            // Profile button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Dark3)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    style = MaterialTheme.typography.labelLarge,
                    color = White
                )
            }
        }

        // Metro line visualization
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Dark2)
                .padding(24.dp)
        ) {
            Column {
                // Blue line indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(MetroBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BLUE LINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetroBlue
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Green line indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(MetroGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GREEN LINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetroGreen
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${stations.size} STATIONS",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray3
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Trip selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "PLAN YOUR TRIP",
                style = MaterialTheme.typography.labelMedium,
                color = Gray3
            )

            Spacer(modifier = Modifier.height(20.dp))

            // From
            StationPickerRow(
                label = "FROM",
                station = selectedSource,
                dotColor = MetroGreen,
                onClick = { onStationClick(true) }
            )

            // Dashed line
            Box(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .width(2.dp)
                    .height(20.dp)
                    .background(Dark4)
            )

            // To
            StationPickerRow(
                label = "TO",
                station = selectedDestination,
                dotColor = MetroBlue,
                onClick = { onStationClick(false) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Search button
            Button(
                onClick = onSearchClick,
                enabled = selectedSource != null && selectedDestination != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Black,
                    disabledContainerColor = Dark4,
                    disabledContentColor = Gray1
                )
            ) {
                Text(
                    text = "SEARCH FARES",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // My Tickets
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "MY TICKETS",
                style = MaterialTheme.typography.labelMedium,
                color = Gray3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Dark3)
                    .clickable { onTicketsClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View ticket history",
                    style = MaterialTheme.typography.bodyLarge,
                    color = White
                )

                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleMedium,
                    color = Gray2
                )
            }
        }
    }
}

@Composable
fun StationPickerRow(
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
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(dotColor)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Gray2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = station?.name ?: "Select station",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (station != null) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (station != null) White else Gray1
            )
        }

        Text(
            text = "→",
            style = MaterialTheme.typography.titleLarge,
            color = Gray1
        )
    }
}
