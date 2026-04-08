package com.pandatern.makco.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
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
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    suspend fun loadTickets() {
        isLoading = true
        error = null
        try {
            val resp = ApiClient.instance.getTickets(token, "chennai")
            if (resp.isSuccessful) {
                val body = resp.body()
                tickets = body ?: emptyList()
            } else {
                error = "Error: ${resp.code()} - ${resp.message()}"
            }
        } catch (e: Exception) {
            error = "Network error: ${e.message}"
        }
        isLoading = false
    }

    LaunchedEffect(Unit) { loadTickets() }

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
            Text("MY TICKETS", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = theme.t1)
            TextButton(onClick = { scope.launch { loadTickets() } }, enabled = !isLoading) {
                Image(painter = painterResource(R.drawable.ic_ticket), contentDescription = null, colorFilter = ColorFilter.tint(theme.t3), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("REFRESH", style = MaterialTheme.typography.labelSmall, color = theme.t3)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(R.drawable.ic_ticket), contentDescription = null, colorFilter = ColorFilter.tint(theme.t4), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(error!!, style = MaterialTheme.typography.bodyMedium, color = theme.t3)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { scope.launch { loadTickets() } }, colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg)) {
                            Text("RETRY")
                        }
                    }
                }
            }
            tickets.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(R.drawable.ic_ticket), contentDescription = null, colorFilter = ColorFilter.tint(theme.t4), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("NO TICKETS YET", style = MaterialTheme.typography.titleMedium, color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your booked tickets will appear here", style = MaterialTheme.typography.bodySmall, color = theme.t4)
                    }
                }
            }
            else -> {
                LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                    items(tickets) { ticket ->
                        TicketCard(ticket = ticket, theme = theme, onClick = { onTicketClick(ticket.bookingId) })
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: BookingStatus, theme: ThemeManager, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, theme.outline)
            .clickable(onClick = onClick)
            .background(theme.bg2)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.ic_ticket), contentDescription = null, colorFilter = ColorFilter.tint(theme.t2), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(ticket.bookingId.take(8).uppercase(), style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = theme.t1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(theme.t2, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(ticket.status, style = MaterialTheme.typography.bodySmall, color = theme.t2)
                }
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₹${ticket.price.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
            Text("VIEW →", style = MaterialTheme.typography.labelSmall, color = theme.t3)
        }
    }
}