package com.pandatern.makco.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    
    // Button scale animation
    val buttonScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(20.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("←", style = MaterialTheme.typography.headlineMedium, color = theme.t1)
            }
            Spacer(modifier = Modifier.width(8.dp))
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
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.error.copy(alpha = 0.15f))
                        .border(3.dp, theme.error, RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Text("PAYMENT FAILED", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error, style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.action, contentColor = theme.bg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("TRY AGAIN", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
            bookingStatus != null -> {
                // Booking summary card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(theme.actionSubtle, theme.bg2)
                            )
                        )
                        .border(3.dp, theme.action, RoundedCornerShape(20.dp))
                        .padding(28.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BOOKING ID", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            bookingStatus.bookingId.take(12).uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = theme.t1
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = theme.divider)
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
                            style = MaterialTheme.typography.bodyLarge,
                            color = theme.t2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // PAY BUTTON - always visible
                Button(
                    onClick = onPayClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .graphicsLayer {
                            scaleX = buttonScale
                            scaleY = buttonScale
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.action,
                        contentColor = theme.bg
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "PAY ₹${bookingStatus.price.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // View ticket link
                TextButton(
                    onClick = onViewTicket,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Already paid? View ticket →",
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