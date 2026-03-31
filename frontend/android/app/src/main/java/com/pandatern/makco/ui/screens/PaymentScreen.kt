package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                            text = "PROCESSING...",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gray500
                        )
                    }
                }
            }
            error != null -> {
                Text(
                    text = "ERROR",
                    style = MaterialTheme.typography.labelLarge,
                    color = Error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray600
                )
            }
            bookingStatus != null -> {
                // Booking ID
                Text(
                    text = "BOOKING",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bookingStatus.bookingId.take(8).uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Mono,
                        fontWeight = FontWeight.Bold
                    ),
                    color = White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Status
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Gray300, RoundedCornerShape(4.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STATUS",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gray500
                        )
                        Text(
                            text = bookingStatus.status,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = when (bookingStatus.status) {
                                "PAYMENT_PENDING" -> White
                                "CONFIRMED" -> GreenLine
                                else -> Gray600
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Gray300, RoundedCornerShape(4.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AMOUNT",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gray500
                        )
                        Text(
                            text = "₹${bookingStatus.price.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

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
                        ),
                        shape = RoundedCornerShape(4.dp)
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
                        text = "Opens Juspay payment page",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (bookingStatus.status == "CONFIRMED") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GreenLine.copy(alpha = 0.2f))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", color = GreenLine)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "PAYMENT CONFIRMED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = GreenLine
                        )
                    }
                }
            }
        }
    }
}
