package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
            .background(Background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Back button
        TextButton(onClick = onBack) {
            Text("< Back", color = Accent)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Route display
        RouteCard(fromStation, toStation)

        Spacer(modifier = Modifier.height(24.dp))

        // Fare options
        Text(
            text = "Select Ticket",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Accent)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Searching fares...",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            error != null -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            color = Error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Retry", color = Background, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            quotes.isEmpty() -> {
                Text(
                    text = "No fares found",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            else -> {
                quotes.forEach { quote ->
                    QuoteCard(
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
fun RouteCard(from: Station?, to: Station?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Success)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = from?.name ?: "Select source",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .width(2.dp)
                    .height(24.dp)
                    .background(Divider)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Error)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = to?.name ?: "Select destination",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun QuoteCard(quote: Quote, onClick: () -> Unit) {
    val label = when (quote.type) {
        "SingleJourney" -> "Single Journey"
        "ReturnJourney" -> "Return Journey"
        else -> quote.type
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Surface2),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "1 Adult",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Text(
                text = "₹${quote.price.toInt()}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Accent
                )
            )
        }
    }
}
