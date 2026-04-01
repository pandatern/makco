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
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TicketScreen(
    token: String,
    bookingId: String,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    var bookingStatus by remember { mutableStateOf<BookingStatus?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isCancelling by remember { mutableStateOf(false) }
    var pollCount by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(bookingId) {
        while (true) {
            try {
                val resp = ApiClient.instance.getBookingStatus(token, bookingId)
                if (resp.isSuccessful) {
                    bookingStatus = resp.body()
                    val status = bookingStatus?.status
                    if (status == "CONFIRMED" || status == "FAILED" || status == "CANCELLED") break
                }
            } catch (_: Exception) {}
            pollCount++
            if (pollCount > 15) break
            delay(2000)
        }
        isLoading = false
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

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("GENERATING TICKET...", style = MaterialTheme.typography.labelMedium, color = theme.t3)
                    }
                }
            }
            bookingStatus != null -> {
                val status = bookingStatus!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    // Booking ID
                    Text("BOOKING", style = MaterialTheme.typography.labelMedium, color = theme.t3)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bookingId.take(8).uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
                        ),
                        color = theme.t1
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Status
                    TicketDetail("STATUS", status.status, when (status.status) {
                        "CONFIRMED" -> Success
                        "PAYMENT_PENDING" -> MetroGold
                        "FAILED", "CANCELLED" -> Error
                        else -> theme.t3
                    }, theme)

                    Spacer(modifier = Modifier.height(12.dp))

                    TicketDetail("AMOUNT", "₹${status.price.toInt()}", theme.t1, theme)

                    Spacer(modifier = Modifier.height(32.dp))

                    // QR Code
                    if (status.status == "CONFIRMED") {
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
                                // QR placeholder - white box with booking ID
                                Box(
                                    modifier = Modifier
                                        .size(180.dp)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("M", style = MaterialTheme.typography.displayLarge.copy(
                                            fontWeight = FontWeight.Black), color = Color.Black)
                                        Text(bookingId.take(8).uppercase(),
                                            style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("CHENNAI METRO", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Valid for single entry + exit",
                                    style = MaterialTheme.typography.bodySmall, color = theme.t4)

                                Spacer(modifier = Modifier.height(12.dp))

                                // Platform info
                                Box(modifier = Modifier.fillMaxWidth().background(theme.bg).padding(12.dp)) {
                                    Column {
                                        Text("PLATFORM INFO", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Blue Line: Platform 1", style = MaterialTheme.typography.bodySmall, color = theme.t3)
                                        Text("Green Line: Platform 2", style = MaterialTheme.typography.bodySmall, color = theme.t3)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Timetable
                                Box(modifier = Modifier.fillMaxWidth().background(theme.bg).padding(12.dp)) {
                                    Column {
                                        Text("FIRST / LAST TRAIN", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("First: 05:30 AM", style = MaterialTheme.typography.bodySmall, color = theme.t3)
                                        Text("Last: 11:00 PM", style = MaterialTheme.typography.bodySmall, color = theme.t3)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Walking directions
                                Box(modifier = Modifier.fillMaxWidth().background(theme.bg).padding(12.dp)) {
                                    Column {
                                        Text("AFTER EXIT", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Follow signs to nearest exit gate", style = MaterialTheme.typography.bodySmall, color = theme.t3)
                                        Text("Show QR at exit scanner", style = MaterialTheme.typography.bodySmall, color = theme.t3)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Share button
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Button(
                            onClick = {
                                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_TEXT,
                                        "Makco Metro Ticket\nBooking: ${bookingId.take(8).uppercase()}\nAmount: ₹${status.price.toInt()}")
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Ticket"))
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.bg3,
                                contentColor = theme.t1
                            )
                        ) {
                            Text("SHARE TICKET", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cancel button
                        Button(
                            onClick = {
                                isCancelling = true
                                scope.launch {
                                    try {
                                        val resp = ApiClient.instance.cancelBooking(token, bookingId)
                                        if (resp.isSuccessful) bookingStatus = resp.body()
                                        else error = "Cancellation failed"
                                    } catch (e: Exception) { error = e.message }
                                    isCancelling = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Error.copy(alpha = 0.1f),
                                contentColor = Error
                            ),
                            enabled = !isCancelling
                        ) {
                            if (isCancelling) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Error, strokeWidth = 2.dp)
                            else Text("CANCEL TICKET", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    // Pending
                    if (status.status == "PAYMENT_PENDING") {
                        Box(
                            modifier = Modifier.fillMaxWidth().background(MetroGold.copy(alpha = 0.1f)).padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AWAITING PAYMENT", style = MaterialTheme.typography.labelLarge, color = MetroGold)
                        }
                    }

                    // Failed/Cancelled
                    if (status.status == "FAILED" || status.status == "CANCELLED") {
                        Box(
                            modifier = Modifier.fillMaxWidth().background(Error.copy(alpha = 0.1f)).padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("BOOKING ${status.status}", style = MaterialTheme.typography.labelLarge, color = Error)
                        }
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Could not load ticket", style = MaterialTheme.typography.bodyMedium, color = theme.t3)
                }
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).background(Error.copy(alpha = 0.1f)).padding(14.dp)) {
                Text(it, color = Error, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun TicketDetail(label: String, value: String, valueColor: Color, theme: ThemeManager) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = theme.t4)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = valueColor)
    }
}
