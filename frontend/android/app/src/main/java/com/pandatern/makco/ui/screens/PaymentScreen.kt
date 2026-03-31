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
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                Text(text = "ERROR", style = MaterialTheme.typography.labelMedium, color = Error)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, style = MaterialTheme.typography.bodySmall, color = Gray500)
            }
            bookingStatus != null -> {
                // Booking ID
                Text(
                    text = "BOOKING",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bookingStatus.bookingId.take(8).uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = White
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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
                            else -> Gray500
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Gray200)
                )

                // Amount
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Gray200)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Pay button or confirmed state
                if (bookingStatus.status == "PAYMENT_PENDING") {
                    Button(
                        onClick = onPayClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
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
                        text = "OPENS JUSPAY",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500
                    )
                }

                if (bookingStatus.status == "CONFIRMED") {
                    Text(
                        text = "✓ PAID",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = GreenLine
                    )
                }
            }
        }
    }
}
