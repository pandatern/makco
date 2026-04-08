package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.pandatern.makco.BuildConfig
import com.pandatern.makco.data.model.BookingResponse
import com.pandatern.makco.data.model.Ticket
import com.pandatern.makco.ui.theme.*

@Composable
fun TicketScreen(
    booking: BookingResponse,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    val ticket = booking.tickets.firstOrNull()
    val qrString = ticket?.qrString ?: ticket?.verificationCode ?: booking.bookingId

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
                    text = booking.bookingId.take(8).uppercase(),
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
                        text = booking.status,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Success
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // DEBUG MODE: Free ticket banner
                if (BuildConfig.IS_DEBUG) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MetroGold.copy(alpha = 0.2f))
                            .border(1.dp, MetroGold, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎉", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "DEBUG MODE - Free Ticket",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MetroGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

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
                        // Metro logo with icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Metro icon (styled)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(MetroBlue, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "M",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "METRO",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 4.sp
                                ),
                                color = MetroBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // REAL QR Code from API
                        if (qrString.isNotEmpty()) {
                            val qrBitmap = remember(qrString) { generateQRBitmap(qrString, 512) }
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(Color.White)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                qrBitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } ?: Text("Generating QR...", color = theme.t4)
                            }
                        } else {
                            // Fallback placeholder if no QR data
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(Color.White)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val pattern = generateQRPattern(booking.bookingId)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    pattern.forEach { row ->
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
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verification Code
                        ticket?.verificationCode?.let { code ->
                            Text(
                                text = code,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 6.sp
                                ),
                                color = if (theme.isDark) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Ticket details
                        Text(
                            text = "₹${booking.price.toInt()} • ${booking.quantity} TICKET${if (booking.quantity > 1) "S" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = theme.t4
                        )

                        // Show station info
                        booking.stations.firstOrNull()?.let { from ->
                            booking.stations.lastOrNull()?.let { to ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${from.name} → ${to.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = theme.t3
                                )
                            }
                        }
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
                            if (booking.type == "ReturnJourney") "Return journey • Entry + Exit" else "Single journey • Entry + Exit",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = theme.t3
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Valid until: ${booking.validTill.take(16).replace("T", " ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = theme.t4
                        )
                    }
                }
            }
        }
    }
}

// Generate real QR bitmap from string
private fun generateQRBitmap(content: String, size: Int): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

// Fallback pattern generator
private fun generateQRPattern(bookingId: String): List<List<Boolean>> {
    val size = 12
    val hash = bookingId.hashCode()
    val pattern = mutableListOf<List<Boolean>>()

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
