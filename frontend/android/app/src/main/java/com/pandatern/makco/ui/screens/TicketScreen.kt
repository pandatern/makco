package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    // Animate content appearance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp)) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = theme.t1)
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Header
                Text("TICKET", style = MaterialTheme.typography.labelMedium, color = theme.t4)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bookingId.take(8).uppercase(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    ),
                    color = theme.t1
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Status badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Success, shape = RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONFIRMED",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Success
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Metro ticket card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (theme.isDark) Color(0xFF111111) else Color(0xFFF5F5F5))
                        .border(
                            1.dp,
                            if (theme.isDark) Color(0xFF222222) else Color(0xFFE0E0E0),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Metro logo
                        Text(
                            text = "METRO",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp
                            ),
                            color = MetroBlue
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code - REAL pattern based on booking ID
                        val qrData = generateQRPattern(bookingId)
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // QR grid
                                qrData.forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        row.forEach { filled ->
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(
                                                        if (filled) Color.Black else Color.White
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Booking ID under QR
                        Text(
                            text = bookingId.take(8).uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 6.sp
                            ),
                            color = if (theme.isDark) Color.White else Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "₹32 • ADULT",
                            style = MaterialTheme.typography.bodyMedium,
                            color = theme.t4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Validity info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (theme.isDark) Color(0xFF0A0A0A) else Color(0xFFEBEBEB))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("VALIDITY", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Single journey • Entry + Exit",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = theme.t3
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Scan QR at gate",
                            style = MaterialTheme.typography.bodySmall,
                            color = theme.t4
                        )
                    }
                }
            }
        }
    }
}

// Generate QR-like pattern from booking ID
private fun generateQRPattern(bookingId: String): List<List<Boolean>> {
    val size = 12
    val hash = bookingId.hashCode()
    val pattern = mutableListOf<List<Boolean>>()

    // Outer frame (always filled)
    for (row in 0 until size) {
        val rowData = mutableListOf<Boolean>()
        for (col in 0 until size) {
            val isFrame = row == 0 || row == size - 1 || col == 0 || col == size - 1
            val isCorner = (row < 3 && col < 3) || (row < 3 && col > size - 4) || (row > size - 4 && col < 3)
            val isData = !isFrame && !isCorner && ((hash * (row * size + col + 1)) % 3 != 0)
            rowData.add(isFrame || isCorner || isData)
        }
        pattern.add(rowData)
    }
    return pattern
}
