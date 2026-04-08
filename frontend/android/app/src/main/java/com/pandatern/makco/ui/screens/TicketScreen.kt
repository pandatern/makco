package com.pandatern.makco.ui.screens

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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.pandatern.makco.R
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.ic_location), contentDescription = "Back", colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(modifier = Modifier.width(12.dp))
            Text("TICKET", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(theme.bg3).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(booking.status, style = MaterialTheme.typography.labelSmall, color = theme.t2)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ticket info card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, theme.outline)
                .background(theme.bg2)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(booking.bookingId.take(8).uppercase(), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace), color = theme.t1)

                Spacer(modifier = Modifier.height(24.dp))

                // Large QR Code
                if (qrString.isNotEmpty()) {
                    val qrBitmap = remember(qrString) { generateQRBitmap(qrString, 300) }
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        qrBitmap?.let {
                            Image(bitmap = it.asImageBitmap(), contentDescription = "QR", modifier = Modifier.fillMaxSize())
                        }
                    }
                } else {
                    Box(modifier = Modifier.size(220.dp).clip(RoundedCornerShape(12.dp)).border(2.dp, theme.outline), contentAlignment = Alignment.Center) {
                        Text("QR", style = MaterialTheme.typography.displayMedium, color = theme.t3)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ticket?.verificationCode?.let { code ->
                    Text(code, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 4.sp), color = theme.t1)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("SCAN AT GATE", style = MaterialTheme.typography.labelSmall, color = theme.t4)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Journey details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, theme.outline)
                .background(theme.bg2)
                .padding(20.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("ROUTE", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                        Spacer(modifier = Modifier.height(4.dp))
                        booking.stations.firstOrNull()?.let { from ->
                            booking.stations.lastOrNull()?.let { to ->
                                Text("${from.name} → ${to.name}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("FARE", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹${booking.price.toInt()} × ${booking.quantity}", style = MaterialTheme.typography.bodyMedium, color = theme.t1)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("VALIDITY", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(booking.validTill.take(10), style = MaterialTheme.typography.bodySmall, color = theme.t2)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TYPE", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(booking.type ?: "Single Journey", style = MaterialTheme.typography.bodySmall, color = theme.t2)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text("Show this ticket at the metro station entrance", style = MaterialTheme.typography.labelSmall, color = theme.t4, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

private fun generateQRBitmap(content: String, size: Int): Bitmap? {
    return try {
        val hints = mapOf(EncodeHintType.MARGIN to 1, EncodeHintType.CHARACTER_SET to "UTF-8")
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) { null }
}