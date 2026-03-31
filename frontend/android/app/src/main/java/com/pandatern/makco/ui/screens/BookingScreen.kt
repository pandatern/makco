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
fun BookingScreen(
    quotes: List<Quote>,
    fromStation: Station?,
    toStation: Station?,
    isLoading: Boolean,
    error: String?,
    onConfirm: (Quote) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Route label
        Text(
            text = "ROUTE",
            style = MaterialTheme.typography.labelMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(12.dp))

        // From
        RouteStation(name = fromStation?.name ?: "...", dotColor = GreenLine)

        Box(
            modifier = Modifier
                .padding(start = 3.dp)
                .width(1.dp)
                .height(12.dp)
                .background(Gray300)
        )

        // To
        RouteStation(name = toStation?.name ?: "...", dotColor = BlueLine)

        Spacer(modifier = Modifier.height(32.dp))

        // Fares label
        Text(
            text = "FARES",
            style = MaterialTheme.typography.labelMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ERROR",
                        style = MaterialTheme.typography.labelMedium,
                        color = Error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onRetry) {
                        Text("RETRY", color = White)
                    }
                }
            }
            quotes.isEmpty() -> {
                Text(
                    text = "No fares found",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }
            else -> {
                quotes.forEach { quote ->
                    FareRow(quote = quote, onClick = { onConfirm(quote) })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun RouteStation(name: String, dotColor: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = White
        )
    }
}

@Composable
fun FareRow(quote: Quote, onClick: () -> Unit) {
    val label = when (quote.type) {
        "SingleJourney" -> "SINGLE"
        "ReturnJourney" -> "RETURN"
        else -> quote.type
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = White
            )
            Text(
                text = "ADULT × 1",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500
            )
        }

        Text(
            text = "₹${quote.price.toInt()}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Gray200)
    )
}
