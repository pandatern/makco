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
import androidx.compose.ui.draw.shadow
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
import com.pandatern.makco.data.model.BookingStatus
import com.pandatern.makco.data.model.Ticket
import com.pandatern.makco.ui.theme.*

@Composable
fun TicketScreen(
    booking: BookingStatus,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    val ticket = booking.tickets.firstOrNull()
    
    val qrString: String = buildString {
        ticket?.let { ticket ->
            val qrSources = listOf(
                ticket.qrCodes?.firstOrNull(),
                ticket.qrString,
                ticket.qRCode,
                ticket.qr_code,
                ticket.qr,
                ticket.verificationCode,
                ticket.ticketNumber,
                ticket.id
            )
            
            qrSources.firstOrNull { it?.isNotBlank() == true } ?: ""
        } ?: ""
        
        if (isEmpty()) {
            append(booking.bookingId)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.bg2)
                    .border(2.dp, theme.outline, RoundedCornerShape(12.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = theme.t1)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("TICKET", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), color = theme.t1)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.action)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(booking.status, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = if (theme.isDark) Black else White)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(theme.bg2)
                .border(3.dp, theme.action, RoundedCornerShape(20.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(booking.bookingId.take(16).uppercase(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp), color = theme.t2)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (qrString.isNotEmpty()) {
                    val qrBitmap = remember(qrString) { generateQRBitmap(qrString, 400) }
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        qrBitmap?.let {
                            Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.fillMaxSize())
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(theme.bg2)
                            .border(3.dp, theme.outline, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No QR Data", style = MaterialTheme.typography.bodyLarge, color = theme.t3)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ticket?.verificationCode?.let { code ->
                    Text(code, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 6.sp), color = theme.t1, textAlign = TextAlign.Center)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("SCAN AT ENTRY GATE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .padding(20.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("ROUTE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        booking.stations?.firstOrNull()?.let { from ->
                            booking.stations?.lastOrNull()?.let { to ->
                                Text(from.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                                Text("to ${to.name}", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("FARE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("₹${booking.price.toInt()}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), color = theme.t1)
                        Text("×${booking.quantity} ticket", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(theme.outline))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("VALID UNTIL", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(4.dp))
                        booking.validTill?.let { Text(it.take(16), style = MaterialTheme.typography.bodyMedium, color = theme.t2) }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TYPE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(booking.vehicleType ?: "METRO", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text("Show this QR code at the metro station entry gate", style = MaterialTheme.typography.labelMedium, color = theme.t4, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(80.dp))
    }
}

private fun generateQRBitmap(content: String, size: Int): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.MARGIN to 2,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}