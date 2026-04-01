package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TicketHistoryScreen(
    token: String,
    onTicketClick: (bookingId: String) -> Unit
) {
    var tickets by remember { mutableStateOf<List<BookingStatus>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = ApiClient.instance.getTickets(token, "chennai")
                if (resp.isSuccessful) {
                    val body = resp.body()
                    tickets = body?.filterIsInstance<BookingStatus>() ?: emptyList()
                }
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "MY TICKETS",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black
            ),
            color = Text1
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                }
            }
            tickets.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO TICKETS YET",
                            style = MaterialTheme.typography.labelMedium,
                            color = Text3
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your metro tickets will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Text4
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(tickets) { ticket ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTicketClick(ticket.bookingId) }
                                .background(Dark3)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ticket.bookingId.take(8).uppercase(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Text1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ticket.status,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when (ticket.status) {
                                        "CONFIRMED" -> Success
                                        "PAYMENT_PENDING" -> MetroGold
                                        "CANCELLED" -> Error
                                        else -> Text3
                                    }
                                )
                            }
                            Text(
                                text = "₹${ticket.price.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Text1
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
