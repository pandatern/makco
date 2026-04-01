package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
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
    val theme = LocalThemeManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black
            ),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "CHENNAI METRO",
            style = MaterialTheme.typography.labelMedium,
            color = theme.t4
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "WHERE TO?",
            style = MaterialTheme.typography.labelMedium,
            color = theme.t3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // From
        StationSlot(
            label = "FROM",
            station = selectedSource,
            dotColor = MetroGreen,
            onClick = { onStationClick(true) },
            theme = theme
        )

        // Connector
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .width(2.dp)
                .height(14.dp)
                .background(theme.divider)
        )

        // To
        StationSlot(
            label = "TO",
            station = selectedDestination,
            dotColor = MetroBlue,
            onClick = { onStationClick(false) },
            theme = theme
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSearchClick,
            enabled = selectedSource != null && selectedDestination != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (theme.isDark) Color.White else Color.Black,
                contentColor = if (theme.isDark) Color.Black else Color.White,
                disabledContainerColor = theme.bg3,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "SEARCH FARES",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun StationSlot(
    label: String,
    station: Station?,
    dotColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    theme: ThemeManager
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(theme.bg2)
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
                color = theme.t4
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = station?.name ?: "Select station",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (station != null) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (station != null) theme.t1 else theme.t4
            )
        }
        Text(text = "→", style = MaterialTheme.typography.titleMedium, color = theme.t4)
    }
}
