package com.pandatern.makco.ui.screens

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    
    // Use QR data from API - prioritize available data
    val qrString: String = buildString {
        // Try in order: qrString from API, verificationCode, ticketNumber, bookingId
        ticket?.qrString?.let { append(it); return@buildString }
        ticket?.verificationCode?.let { append(it); return@buildString }
        ticket?.ticketNumber?.let { append(it); return@buildString }
        append(booking.bookingId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(20.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.ic_location), contentDescription = "Back", colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(modifier = Modifier.width(12.dp))
            Text("TICKET", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), color = theme.t1)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(theme.actionSubtle).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(booking.status, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ticket card with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(theme.bg2)
                .border(3.dp, theme.action, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Booking ID
                Text(booking.bookingId.take(16).uppercase(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace), color = theme.t2)
                
                Spacer(modifier = Modifier.height(24.dp))

                // QR Code
                if (qrString.isNotEmpty()) {
                    val qrBitmap = remember(qrString) { generateQRBitmap(qrString, 300) }
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        qrBitmap?.let {
                            Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.fillMaxSize())
                        }
                    }
                } else {
                    Box(modifier = Modifier.size(260.dp).clip(RoundedCornerShape(16.dp)).border(3.dp, theme.outline), contentAlignment = Alignment.Center) {
                        Text("No QR Data", style = MaterialTheme.typography.bodyLarge, color = theme.t3)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification code
                ticket?.verificationCode?.let { code ->
                    Text(code, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 6.sp), color = theme.t1, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("SCAN AT ENTRY GATE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Journey details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, theme.outline, RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .padding(20.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("ROUTE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        booking.stations.firstOrNull()?.let { from ->
                            booking.stations.lastOrNull()?.let { to ->
                                Text("${from.name}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                                Text("to ${to.name}", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("FARE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("₹${booking.price.toInt()}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), color = theme.t1)
                        Text("×${booking.quantity} ticket", style = MaterialTheme.typography.bodySmall, color = theme.t2)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = theme.divider)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("VALID UNTIL", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(4.dp))
                        booking.validTill?.let { Text(it.take(16), style = MaterialTheme.typography.bodyMedium, color = theme.t2) }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TYPE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(booking.type ?: "Single Journey", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text("Show this QR code at the metro station entry gate", style = MaterialTheme.typography.labelMedium, color = theme.t4, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(80.dp))
    }
}

// Build proper QR string matching Chennai Metro format
private fun buildQrString(booking: BookingResponse, ticket: Ticket?): String {
    return when {
        // Priority: Use ticket's verification code if available
        ticket?.verificationCode != null -> {
            // Chennai Metro QR format: verification_code|booking_id|station_code
            val stationCode = booking.stations.firstOrNull()?.code ?: ""
            "${ticket.verificationCode}|${booking.bookingId}|$stationCode"
        }
        // Use ticket's qrString if available
        ticket?.qrString != null -> ticket.qrString
        // Fallback: booking ID with format
        booking.bookingId.isNotEmpty() -> {
            "MAKCO:${booking.bookingId}|${booking.price.toInt()}|${booking.quantity}"
        }
        else -> ""
    }
}

// Generate QR with proper encoding for metro scanners
private fun generateQRBitmap(content: String, size: Int): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H
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
    } catch (e: Exception) { null }
}