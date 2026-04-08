package com.pandatern.makco.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = theme.t1)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header - plain text
            Text("TICKET", style = MaterialTheme.typography.labelMedium, color = theme.t3)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = booking.bookingId.take(8).uppercase(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status - plain text
            Text(booking.status, style = MaterialTheme.typography.labelMedium, color = theme.t2)

            // DEBUG banner - mono
            if (BuildConfig.IS_DEBUG) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, theme.t1)
                        .padding(12.dp)
                ) {
                    Text(
                        "DEBUG MODE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = theme.t1
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // QR box - simple bordered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(2.dp, theme.t1)
                    .background(theme.bg2)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrString.isNotEmpty()) {
                    val qrBitmap = remember(qrString) { generateQRBitmap(qrString, 400) }
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Text("QR", style = MaterialTheme.typography.headlineLarge, color = theme.t3)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info - plain text
            Text(
                "₹${booking.price.toInt()} × ${booking.quantity}",
                style = MaterialTheme.typography.titleMedium,
                color = theme.t1
            )

            booking.stations.firstOrNull()?.let { from ->
                booking.stations.lastOrNull()?.let { to ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${from.name} → ${to.name}", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                }
            }
        }
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