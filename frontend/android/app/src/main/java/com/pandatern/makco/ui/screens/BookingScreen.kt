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
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(start = 12.dp)) {
            TextButton(onClick = onBack) {
                Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Route
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "ROUTE",
                style = MaterialTheme.typography.labelMedium,
                color = Gray3
            )

            Spacer(modifier = Modifier.height(16.dp))

            RouteStation(name = fromStation?.name ?: "...", dotColor = MetroGreen)

            Box(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .width(2.dp)
                    .height(16.dp)
                    .background(Dark4)
            )

            RouteStation(name = toStation?.name ?: "...", dotColor = MetroBlue)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Fares
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "FARES",
                style = MaterialTheme.typography.labelMedium,
                color = Gray3
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Error.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = error,
                                color = Error,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = onRetry) {
                            Text("RETRY", color = White)
                        }
                    }
                }
                quotes.isEmpty() -> {
                    Text(
                        text = "No fares found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray2
                    )
                }
                else -> {
                    quotes.forEach { quote ->
                        FareRow(quote = quote, onClick = { onConfirm(quote) })
                    }
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
                .size(12.dp)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = White
        )
    }
}

@Composable
fun FareRow(quote: Quote, onClick: () -> Unit) {
    val label = when (quote.type) {
        "SingleJourney" -> "SINGLE JOURNEY"
        "ReturnJourney" -> "RETURN JOURNEY"
        else -> quote.type
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Dark3)
            .padding(20.dp),
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
                color = Gray2
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

    Spacer(modifier = Modifier.height(8.dp))
}
