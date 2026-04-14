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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import com.pandatern.makco.data.local.SecureCacheManager
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun TicketHistoryScreen(
    token: String,
    onTicketClick: (bookingId: String) -> Unit
) {
    val theme = LocalThemeManager.current
    val context = LocalContext.current
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
                if (tickets.isNotEmpty()) {
                    SecureCacheManager.saveTickets(context, tickets)
                }
            } else {
                val cached = SecureCacheManager.getTickets(context)
                if (cached != null && cached.isNotEmpty()) {
                    tickets = cached
                } else {
                    tickets = SecureCacheManager.getBookingHistory(context)
                }
            }
        } catch (e: Exception) {
            val cachedTickets = SecureCacheManager.getTickets(context)
            if (cachedTickets != null && cachedTickets.isNotEmpty()) {
                tickets = cachedTickets
            } else {
                tickets = SecureCacheManager.getBookingHistory(context)
            }
        }
        
        if (tickets.isEmpty()) {
            tickets = SecureCacheManager.getBookingHistory(context)
        }
        
        isLoading = false
    }

    LaunchedEffect(Unit) { loadTickets() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("MY TICKETS", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = theme.t1)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.bg2)
                    .border(2.dp, theme.outline, RoundedCornerShape(12.dp))
                    .clickable { scope.launch { loadTickets() } },
                contentAlignment = Alignment.Center
            ) {
                Text("↻", style = MaterialTheme.typography.titleMedium, color = theme.t2)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.action, strokeWidth = 3.dp)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(R.drawable.ic_ticket), contentDescription = null, colorFilter = ColorFilter.tint(theme.t4), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(error!!, style = MaterialTheme.typography.bodyLarge, color = theme.t3)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.action)
                                .border(2.dp, theme.outline, RoundedCornerShape(12.dp))
                                .clickable { scope.launch { loadTickets() } },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("RETRY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = if (theme.isDark) Black else White)
                        }
                    }
                }
            }
            tickets.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(R.drawable.ic_ticket), contentDescription = null, colorFilter = ColorFilter.tint(theme.t4), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("NO TICKETS YET", style = MaterialTheme.typography.titleLarge, color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your booked tickets will appear here", style = MaterialTheme.typography.bodyMedium, color = theme.t4)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
            .background(theme.bg2)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.ic_ticket), contentDescription = null, colorFilter = ColorFilter.tint(theme.action), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(ticket.bookingId.take(8).uppercase(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = theme.t1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(theme.action, RoundedCornerShape(4.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(ticket.status, style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${ticket.price.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                Text("VIEW →", style = MaterialTheme.typography.labelMedium, color = theme.t3)
            }
        }
    }
}