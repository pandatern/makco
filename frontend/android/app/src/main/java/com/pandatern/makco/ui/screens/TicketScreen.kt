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
import androidx.compose.ui.unit.sp
import com.pandatern.makco.ui.theme.*

@Composable
fun TicketScreen(
    bookingId: String,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current

    // Show immediately - no API call, no loading
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

        // Booking ID
        Text("BOOKING", style = MaterialTheme.typography.labelMedium, color = theme.t4)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = bookingId.take(8).uppercase(),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
            ),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Status
        Text("STATUS", style = MaterialTheme.typography.labelSmall, color = theme.t4)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "CONFIRMED",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Success
        )

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code - VISIBLE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // QR pattern - grid of small squares
                repeat(8) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(8) { col ->
                            val filled = (row + col + bookingId.hashCode()) % 3 != 0
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(if (filled) Color.Black else Color.LightGray)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = bookingId.take(8).uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    ),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("CHENNAI METRO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Validity
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.bg2)
                .padding(16.dp)
        ) {
            Column {
                Text("VALIDITY", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Valid for single entry + exit", style = MaterialTheme.typography.bodyMedium, color = theme.t3)
                Text("Scan at gate", style = MaterialTheme.typography.bodySmall, color = theme.t4)
            }
        }
    }
}
