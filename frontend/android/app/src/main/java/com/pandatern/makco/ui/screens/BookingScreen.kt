package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.*
import com.pandatern.makco.ui.theme.*

@Composable
fun BookingScreen(
    quotes: List<Quote>,
    fromStation: Station?,
    toStation: Station?,
    isLoading: Boolean,
    error: String?,
    onConfirm: (quote: Quote, quantity: Int) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }
    var quantity by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(R.drawable.ic_location), contentDescription = "Back", colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(modifier = Modifier.width(12.dp))
            Text("BOOK", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Route visualization
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, theme.outline)
                .background(theme.bg2)
                .padding(20.dp)
        ) {
            Column {
                // From
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(theme.t1, RoundedCornerShape(6.dp)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("FROM", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(fromStation?.name ?: "...", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)

                Spacer(modifier = Modifier.height(12.dp))

                // Line
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(12.dp).height(1.dp).background(theme.t3))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.width(8.dp).height(8.dp).background(theme.t3, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.width(40.dp).height(1.dp).background(theme.t3))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // To
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(theme.t2, RoundedCornerShape(6.dp)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("TO", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(toStation?.name ?: "...", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("SELECT TICKET TYPE", style = MaterialTheme.typography.labelMedium, color = theme.t4)
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(theme.bg2).border(1.dp, theme.t1).padding(16.dp)) {
                    Text(error, color = theme.t1, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg)) {
                    Text("RETRY")
                }
            }
            quotes.isEmpty() -> {
                Text("No tickets available", color = theme.t3)
            }
            else -> {
                quotes.forEach { quote ->
                    val isSelected = selectedQuote?.quoteId == quote.quoteId
                    val label = if (quote.type == "SingleJourney") "SINGLE JOURNEY" else "RETURN JOURNEY"
                    val desc = if (quote.type == "SingleJourney") "One-way trip" else "Round trip - same day"

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, if (isSelected) theme.t1 else theme.outline)
                            .background(if (isSelected) theme.bg3 else theme.bg2)
                            .clickable { selectedQuote = quote }
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = theme.t3)
                            }
                            Text("₹${quote.price.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (selectedQuote != null && !isLoading) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("QUANTITY", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, theme.outline)
                        .clickable { if (quantity > 1) quantity-- },
                    contentAlignment = Alignment.Center
                ) {
                    Text("−", style = MaterialTheme.typography.titleLarge, color = if (quantity > 1) theme.t1 else theme.t4)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text("$quantity", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, theme.outline)
                        .clickable { if (quantity < 10) quantity++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.titleLarge, color = if (quantity < 10) theme.t1 else theme.t4)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("₹${(selectedQuote!!.price * quantity).toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConfirm(selectedQuote!!, quantity) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("PAY ₹${(selectedQuote!!.price * quantity).toInt()}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}