package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val theme = LocalThemeManager.current
    var tickets by remember { mutableStateOf<List<BookingStatus>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val outline = if (theme.isDark) Color(0xFF2A2A2A) else Color(0xFFD0D0D0)

    suspend fun loadTickets() {
        try {
            val resp = ApiClient.instance.getTickets(token, "chennai")
            if (resp.isSuccessful) tickets = resp.body() ?: emptyList()
        } catch (_: Exception) {}
        isLoading = false
    }

    LaunchedEffect(Unit) { loadTickets() }

    fun refresh() {
        isRefreshing = true
        scope.launch {
            loadTickets()
            isRefreshing = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MY TICKETS",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = theme.t1
            )

            // Refresh button
            if (!isLoading) {
                TextButton(
                    onClick = { refresh() },
                    enabled = !isRefreshing
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = theme.t4, strokeWidth = 2.dp)
                    } else {
                        Text("REFRESH", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                }
            }
            tickets.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NO TICKETS YET", style = MaterialTheme.typography.labelMedium, color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { refresh() }) {
                            Text("REFRESH", style = MaterialTheme.typography.labelMedium, color = theme.t1)
                        }
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
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, outline, RoundedCornerShape(8.dp))
                                .clickable { onTicketClick(ticket.bookingId) }
                                .background(theme.bg)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ticket.bookingId.take(8).uppercase(),
                                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
                                    color = theme.t1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ticket.status,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when (ticket.status) {
                                        "CONFIRMED" -> Success
                                        "PAYMENT_PENDING" -> MetroGold
                                        "CANCELLED" -> Error
                                        else -> theme.t3
                                    }
                                )
                            }
                            Text(
                                text = "₹${ticket.price.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = theme.t1
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
