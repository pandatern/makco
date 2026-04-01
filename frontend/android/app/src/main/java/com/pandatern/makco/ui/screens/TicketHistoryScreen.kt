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
    onTicketClick: (bookingId: String) -> Unit,
    onBack: () -> Unit
) {
    var tickets by remember { mutableStateOf<List<BookingStatus>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = ApiClient.instance.getTickets(token, "chennai")
                if (resp.isSuccessful) {
                    val body = resp.body()
                    // The API returns a list - try to parse as BookingStatus
                    // If it fails, we'll show empty
                    tickets = body?.filterIsInstance<BookingStatus>() ?: emptyList()
                }
            } catch (e: Exception) {
                error = e.message
            }
            isLoading = false
        }
    }

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

        Text(
            text = "MY TICKETS",
            style = MaterialTheme.typography.labelMedium,
            color = Gray3,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "Error loading tickets",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Error
                    )
                }
            }
            tickets.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO TICKETS",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gray2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your metro tickets will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray1
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketHistoryItem(
                            ticket = ticket,
                            onClick = { onTicketClick(ticket.bookingId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketHistoryItem(
    ticket: BookingStatus,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                color = White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ticket.status,
                style = MaterialTheme.typography.bodySmall,
                color = when (ticket.status) {
                    "CONFIRMED" -> Success
                    "PAYMENT_PENDING" -> MetroInterchange
                    "CANCELLED" -> Error
                    else -> Gray2
                }
            )
        }

        Text(
            text = "₹${ticket.price.toInt()}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = White
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}
