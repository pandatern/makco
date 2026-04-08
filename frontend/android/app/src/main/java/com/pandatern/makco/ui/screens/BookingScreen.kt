package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

        TextButton(onClick = onBack) {
            Text("← BACK", style = MaterialTheme.typography.labelLarge, color = theme.t1)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Route - plain text
        Text("ROUTE", style = MaterialTheme.typography.labelMedium, color = theme.t3)
        Spacer(modifier = Modifier.height(12.dp))

        Text(fromStation?.name ?: "...", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
        Text("→", style = MaterialTheme.typography.bodyLarge, color = theme.t3)
        Text(toStation?.name ?: "...", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)

        Spacer(modifier = Modifier.height(24.dp))

        // Fares - plain text
        Text("SELECT TICKET", style = MaterialTheme.typography.labelMedium, color = theme.t3)
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxWidth().border(2.dp, theme.t1).padding(16.dp)) {
                    Text(error, color = theme.t1, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onRetry) { Text("RETRY", color = theme.t1) }
            }
            quotes.isEmpty() -> {
                Text("No fares found", style = MaterialTheme.typography.bodyMedium, color = theme.t3)
            }
            else -> {
                // Single/Return options - simple bordered boxes
                quotes.forEach { quote ->
                    val isSelected = selectedQuote?.quoteId == quote.quoteId
                    val label = if (quote.type == "SingleJourney") "SINGLE" else if (quote.type == "ReturnJourney") "RETURN" else quote.type

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, if (isSelected) theme.t1 else theme.outline)
                            .background(if (isSelected) theme.bg3 else theme.bg2)
                            .clickable { selectedQuote = quote }
                            .padding(18.dp)
                    ) {
                        Column {
                            Text(label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                            Text("₹${quote.price.toInt()}", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Quantity
        if (selectedQuote != null && !isLoading) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("QUANTITY", style = MaterialTheme.typography.labelMedium, color = theme.t3)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(2.dp, theme.outline)
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
                        .size(40.dp)
                        .border(2.dp, theme.outline)
                        .clickable { if (quantity < 6) quantity++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.titleLarge, color = if (quantity < 6) theme.t1 else theme.t4)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("₹${(selectedQuote!!.price * quantity).toInt()}", style = MaterialTheme.typography.titleLarge, color = theme.t1)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConfirm(selectedQuote!!, quantity) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.t1,
                    contentColor = theme.bg
                )
            ) {
                Text("CONFIRM ₹${(selectedQuote!!.price * quantity).toInt()}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}