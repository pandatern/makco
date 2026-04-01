package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TicketScreen(
    token: String,
    bookingId: String,
    onBack: () -> Unit
) {
    var bookingStatus by remember { mutableStateOf<BookingStatus?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var pollCount by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Poll for status until CONFIRMED
    LaunchedEffect(bookingId) {
        while (true) {
            try {
                val resp = com.pandatern.makco.data.remote.ApiClient.instance.getBookingStatus(token, bookingId)
                if (resp.isSuccessful) {
                    bookingStatus = resp.body()
                    val status = bookingStatus?.status
                    if (status == "CONFIRMED" || status == "FAILED" || status == "CANCELLED") {
                        break
                    }
                }
            } catch (_: Exception) {}
            pollCount++
            if (pollCount > 20) break // Max 20 polls
            delay(3000) // Poll every 3 seconds
        }
        isLoading = false
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

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "GENERATING TICKET...",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gray3
                        )
                        if (pollCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ATTEMPT $pollCount",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gray1
                            )
                        }
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
                    Text(
                        text = "BOOKING",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gray3
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bookingId.take(8).uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = White
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Status
                    TicketDetailRow("STATUS", status.status, when (status.status) {
                        "CONFIRMED" -> Success
                        "PAYMENT_PENDING" -> MetroInterchange
                        "FAILED" -> Error
                        "CANCELLED" -> Error
                        else -> White
                    })

                    Spacer(modifier = Modifier.height(12.dp))

                    // Amount
                    TicketDetailRow("AMOUNT", "₹${status.price.toInt()}", White)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quantity
                    TicketDetailRow("QUANTITY", "${status.quantity ?: 1}", White)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Type
                    TicketDetailRow("TYPE", status.type ?: "SINGLE", White)

                    Spacer(modifier = Modifier.height(32.dp))

                    // QR Code area (only if CONFIRMED)
                    if (status.status == "CONFIRMED") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Dark3)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCAN AT GATE",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MetroBlue
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // QR placeholder - shows booking ID as text
                                // TODO: Generate actual QR from unifiedQRV2 data
                                Box(
                                    modifier = Modifier
                                        .size(180.dp)
                                        .background(White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "M",
                                            style = MaterialTheme.typography.displayLarge.copy(
                                                fontWeight = FontWeight.Black
                                            ),
                                            color = Black
                                        )
                                        Text(
                                            text = bookingId.take(8).uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Gray1
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "CHENNAI METRO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray2
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Validity info
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Dark2)
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "VALIDITY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Valid for single entry + exit",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray4
                                )
                                Text(
                                    text = "Governed by CMRL business rules",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Gray2
                                )
                            }
                        }
                    }

                    // Pending state
                    if (status.status == "PAYMENT_PENDING") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MetroInterchange.copy(alpha = 0.1f))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AWAITING PAYMENT",
                                style = MaterialTheme.typography.labelLarge,
                                color = MetroInterchange
                            )
                        }
                    }

                    // Failed state
                    if (status.status == "FAILED" || status.status == "CANCELLED") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Error.copy(alpha = 0.1f))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BOOKING ${status.status}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Error
                            )
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Could not load ticket",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray2
                    )
                }
            }
        }
    }
}

@Composable
fun TicketDetailRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Gray2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor
        )
    }
}
