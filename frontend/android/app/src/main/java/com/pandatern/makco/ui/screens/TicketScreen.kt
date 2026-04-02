package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.data.local.CacheManager
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TicketScreen(
    token: String,
    bookingId: String,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var bookingStatus by remember { mutableStateOf<BookingStatus?>(null) }

    val scope = rememberCoroutineScope()

    // Fetch in background, show immediately
    LaunchedEffect(bookingId) {
        scope.launch {
            try {
                val resp = ApiClient.instance.getBookingStatus(token, bookingId)
                if (resp.isSuccessful) {
                    bookingStatus = resp.body()
                    // Save to history
                    bookingStatus?.let { CacheManager.addBookingHistory(ctx, it) }
                }
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(start = 12.dp)) {
            TextButton(onClick = onBack) {
                Text("← BACK", style = MaterialTheme.typography.labelLarge, color = theme.t1)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Booking ID
            Text("BOOKING", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bookingId.take(8).uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black
                ),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Status - always CONFIRMED for debug
            Text("STATUS", style = MaterialTheme.typography.labelSmall, color = theme.t4)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "CONFIRMED",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Success
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount
            Text("AMOUNT", style = MaterialTheme.typography.labelSmall, color = theme.t4)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹${bookingStatus?.price?.toInt() ?: 32}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(32.dp))

            // QR Code
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(theme.bg2)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SCAN AT GATE", style = MaterialTheme.typography.labelMedium, color = MetroBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = bookingId.take(8).uppercase(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
                                ),
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "₹${bookingStatus?.price?.toInt() ?: 32}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("METRO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("CHENNAI METRO", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Valid for single entry + exit", style = MaterialTheme.typography.bodySmall, color = theme.t4)
                }
            }
        }
    }
}
