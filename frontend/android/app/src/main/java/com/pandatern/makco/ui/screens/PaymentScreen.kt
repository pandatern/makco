package com.pandatern.makco.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.BookingResponse
import com.pandatern.makco.ui.theme.*

@Composable
fun PaymentScreen(
    bookingStatus: BookingResponse?,
    isLoading: Boolean,
    error: String?,
    onPayClick: () -> Unit,
    onViewTicket: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    var showPaymentFlow by remember { mutableStateOf(true) }

    // Animate the pay button
    val buttonScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Back button with animation
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
                // Error state with gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(theme.error.copy(alpha = 0.2f), theme.bg)
                            )
                        )
                        .border(2.dp, theme.error, RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("ERROR", style = MaterialTheme.typography.labelMedium, color = theme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error, color = theme.t2, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("RETRY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
            bookingStatus != null -> {
                // Booking card with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(theme.actionSubtle, theme.bg2)
                            )
                        )
                        .border(2.dp, theme.outline, RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BOOKING ID", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bookingStatus.bookingId.take(12).uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
                            ),
                            color = theme.t1
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(color = theme.divider)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Amount with large display
                        Text("TOTAL AMOUNT", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "₹${bookingStatus.price.toInt()}",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                            color = theme.t1
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${bookingStatus.quantity} Ticket${if (bookingStatus.quantity > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium, color = theme.t3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Payment action button - always show PAY button
                if (showPaymentFlow) {
                    Button(
                        onClick = {
                            showPaymentFlow = false
                            onPayClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .graphicsLayer {
                                scaleX = buttonScale
                                scaleY = buttonScale
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = theme.action,
                            contentColor = theme.bg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = theme.bg, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                "PAY ₹${bookingStatus.price.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Or view ticket (for already paid)
                    TextButton(
                        onClick = onViewTicket,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Already paid? View ticket",
                            style = MaterialTheme.typography.labelMedium,
                            color = theme.t3
                        )
                    }
                } else {
                    // Processing state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.actionSubtle)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Processing payment...", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                        }
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No booking data", color = theme.t3)
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