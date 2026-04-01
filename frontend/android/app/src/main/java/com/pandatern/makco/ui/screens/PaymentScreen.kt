package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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
    onViewTicket: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = theme.t1)
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxWidth().background(Error.copy(alpha = 0.1f)).padding(16.dp)) {
                    Text(error, color = Error, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (theme.isDark) Color.White else Color.Black,
                        contentColor = if (theme.isDark) Color.Black else Color.White
                    )
                ) {
                    Text("RETRY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
            bookingStatus != null -> {
                val status = bookingStatus!!

                // Booking ID
                Text("BOOKING", style = MaterialTheme.typography.labelMedium, color = theme.t3)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = status.bookingId.take(8).uppercase(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
                    ),
                    color = theme.t1
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Status
                PaymentRow("STATUS", status.status, when (status.status) {
                    "PAYMENT_PENDING" -> MetroGold
                    "CONFIRMED" -> Success
                    "FAILED", "CANCELLED" -> Error
                    else -> theme.t3
                }, theme)

                Spacer(modifier = Modifier.height(12.dp))

                // Amount
                PaymentRow("AMOUNT", "₹${status.price.toInt()}", theme.t1, theme)

                // Expiry warning
                if (status.status == "PAYMENT_PENDING") {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxWidth().background(MetroGold.copy(alpha = 0.1f)).padding(14.dp)) {
                        Text("Complete payment within 10 minutes or booking will expire",
                            style = MaterialTheme.typography.bodySmall, color = MetroGold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Pay button
                if (status.status == "PAYMENT_PENDING") {
                    Button(
                        onClick = onPayClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (theme.isDark) Color.White else Color.Black,
                            contentColor = if (theme.isDark) Color.Black else Color.White
                        )
                    ) {
                        Text("PAY ₹${status.price.toInt()}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }

                // Confirmed
                if (status.status == "CONFIRMED") {
                    Button(
                        onClick = onViewTicket,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (theme.isDark) Color.White else Color.Black,
                            contentColor = if (theme.isDark) Color.Black else Color.White
                        )
                    ) {
                        Text("VIEW TICKET",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }

                // Failed - retry booking
                if (status.status == "FAILED" || status.status == "CANCELLED") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (theme.isDark) Color.White else Color.Black,
                            contentColor = if (theme.isDark) Color.Black else Color.White
                        )
                    ) {
                        Text("TRY AGAIN",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentRow(label: String, value: String, valueColor: Color, theme: ThemeManager) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = theme.t4)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = valueColor)
    }
}
