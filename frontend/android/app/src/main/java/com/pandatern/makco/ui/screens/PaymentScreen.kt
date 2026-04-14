package com.pandatern.makco.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.data.model.BookingStatus
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
            Text("PAYMENT", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), color = theme.t1)
        }

        Spacer(modifier = Modifier.height(32.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 3.dp)
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.t4)
                        .border(3.dp, theme.t1, RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Text("PAYMENT FAILED", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error, style = MaterialTheme.typography.bodyLarge, color = theme.t2)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.t1)
                        .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
                        .clickable { onRetry() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("TRY AGAIN", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (theme.isDark) Black else White)
                }
            }
            bookingStatus != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(theme.bg2)
                        .border(3.dp, theme.t1, RoundedCornerShape(20.dp))
                        .padding(28.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BOOKING ID", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            bookingStatus.bookingId.take(12).uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                            color = theme.t1
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(theme.outline))
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("TOTAL", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "₹${bookingStatus.price.toInt()}",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                            color = theme.t1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${bookingStatus.quantity} Ticket",
                            style = MaterialTheme.typography.bodyLarge, color = theme.t2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.t1)
                        .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
                        .clickable { onPayClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "PAY ₹${bookingStatus.price.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (theme.isDark) Black else White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(theme.bg2)
                        .border(2.dp, theme.outline, RoundedCornerShape(12.dp))
                        .clickable { onViewTicket() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "ALREADY PAID? VIEW TICKET →",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = theme.t2
                    )
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No booking found", style = MaterialTheme.typography.bodyLarge, color = theme.t3)
                }
            }
        }
    }
}