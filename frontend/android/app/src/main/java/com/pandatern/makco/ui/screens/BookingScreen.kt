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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Route
        Text(
            text = "ROUTE",
            style = MaterialTheme.typography.labelMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(12.dp))

        // From
        RouteStationRow(
            name = fromStation?.name ?: "...",
            code = fromStation?.code ?: "",
            dotColor = GreenLine
        )

        // Connector
        Box(
            modifier = Modifier
                .padding(start = 14.dp)
                .width(1.dp)
                .height(20.dp)
                .background(Gray400)
        )

        // To
        RouteStationRow(
            name = toStation?.name ?: "...",
            code = toStation?.code ?: "",
            dotColor = BlueLine
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Fares
        Text(
            text = "FARES",
            style = MaterialTheme.typography.labelMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "SEARCHING...",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gray500
                        )
                    }
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Error, RoundedCornerShape(4.dp))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ERROR",
                            style = MaterialTheme.typography.labelLarge,
                            color = Error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray600
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = onRetry) {
                            Text("RETRY", color = White)
                        }
                    }
                }
            }
            quotes.isEmpty() -> {
                Text(
                    text = "No fares available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray500
                )
            }
            else -> {
                quotes.forEach { quote ->
                    FareCard(
                        quote = quote,
                        onClick = { onConfirm(quote) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun RouteStationRow(
    name: String,
    code: String,
    dotColor: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = White,
                    fontWeight = FontWeight.Medium
                )
            )
            if (code.isNotEmpty()) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500
                )
            }
        }
    }
}

@Composable
fun FareCard(
    quote: Quote,
    onClick: () -> Unit
) {
    val label = when (quote.type) {
        "SingleJourney" -> "SINGLE JOURNEY"
        "ReturnJourney" -> "RETURN JOURNEY"
        else -> quote.type.uppercase()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Gray300, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ADULT × 1",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
                quote.categories?.firstOrNull()?.let { cat ->
                    Text(
                        text = cat.categoryMeta?.description ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500
                    )
                }
            }

            Text(
                text = "₹${quote.price.toInt()}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )
        }
    }
}
