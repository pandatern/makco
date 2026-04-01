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

@Composable
fun PaymentScreen(
    bookingStatus: BookingStatus?,
    isLoading: Boolean,
    error: String?,
    onPayClick: () -> Unit,
    onViewTicket: () -> Unit,
    onBack: () -> Unit
) {
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
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(Error.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Text(text = error, color = Error, style = MaterialTheme.typography.labelMedium)
                }
            }
            bookingStatus != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    // Booking ID
                    Text(
                        text = "BOOKING",
                        style = MaterialTheme.typography.labelMedium,
                        color = Text3
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bookingStatus.bookingId.take(8).uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = White
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Status
                    PaymentDetailRow(
                        label = "STATUS",
                        value = bookingStatus.status,
                        valueColor = when (bookingStatus.status) {
                            "PAYMENT_PENDING" -> MetroGold
                            "CONFIRMED" -> Success
                            else -> White
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Amount
                    PaymentDetailRow(
                        label = "AMOUNT",
                        value = "₹${bookingStatus.price.toInt()}",
                        valueColor = White
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Pay button
                    if (bookingStatus.status == "PAYMENT_PENDING") {
                        Button(
                            onClick = onPayClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = White,
                                contentColor = Black
                            )
                        ) {
                            Text(
                                text = "PAY ₹${bookingStatus.price.toInt()}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "OPENS JUSPAY PAYMENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Text3
                        )
                    }

                    if (bookingStatus.status == "CONFIRMED") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Success.copy(alpha = 0.1f))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓ PAID",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Success
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onViewTicket,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = White,
                                contentColor = Black
                            )
                        ) {
                            Text(
                                text = "VIEW TICKET",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentDetailRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Text3
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
