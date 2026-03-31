package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onBack) {
                Text("< Back", color = Accent)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(color = Accent)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Processing booking...",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            error != null -> {
                Text(
                    text = "Error",
                    color = Error,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            bookingStatus?.status == "PAYMENT_PENDING" -> {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(AccentGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₹",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = Accent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "₹${bookingStatus.price.toInt()}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Payment Pending",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Warning
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onPayClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Pay Now",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Background,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            bookingStatus?.status == "CONFIRMED" -> {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(Success.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", fontSize = 40.sp, color = Success)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Payment Successful",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Success
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "₹${bookingStatus.price.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }
            else -> {
                Text(
                    text = "Booking Created",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bookingStatus?.status ?: "Processing...",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
