package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        // Route
        Text("ROUTE", style = MaterialTheme.typography.labelMedium, color = theme.t3)
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(MetroGreen))
            Spacer(modifier = Modifier.width(12.dp))
            Text(fromStation?.name ?: "...", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = theme.t1)
        }
        Box(modifier = Modifier.padding(start = 4.dp).width(2.dp).height(12.dp).background(theme.divider))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(MetroBlue))
            Spacer(modifier = Modifier.width(12.dp))
            Text(toStation?.name ?: "...", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = theme.t1)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Fares
        Text("SELECT TICKET", style = MaterialTheme.typography.labelMedium, color = theme.t3)
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxWidth().background(Error.copy(alpha = 0.1f)).padding(16.dp)) {
                    Text(error, color = Error, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onRetry) { Text("RETRY", color = theme.t1) }
            }
            quotes.isEmpty() -> {
                Text("No fares found", style = MaterialTheme.typography.bodyMedium, color = theme.t4)
            }
            else -> {
                quotes.forEach { quote ->
                    val isSelected = selectedQuote?.quoteId == quote.quoteId
                    val label = when (quote.type) {
                        "SingleJourney" -> "SINGLE"
                        "ReturnJourney" -> "RETURN"
                        else -> quote.type
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedQuote = quote }
                            .background(if (isSelected) theme.bg3 else theme.bg2)
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                            Text("ADULT", style = MaterialTheme.typography.bodySmall, color = theme.t4)
                            // Fare breakdown
                            quote.categories?.firstOrNull()?.let { cat ->
                                cat.categoryMeta?.description?.let { desc ->
                                    if (desc.isNotEmpty()) Text(desc, style = MaterialTheme.typography.bodySmall, color = theme.t4)
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("₹${quote.price.toInt()}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(modifier = Modifier.size(8.dp).background(MetroBlue))
                            }
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
                    modifier = Modifier.size(40.dp).background(if (quantity > 1) theme.bg3 else theme.bg2)
                        .clickable { if (quantity > 1) quantity-- },
                    contentAlignment = Alignment.Center
                ) {
                    Text("−", style = MaterialTheme.typography.headlineMedium, color = if (quantity > 1) theme.t1 else theme.t4)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text("$quantity", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier.size(40.dp).background(if (quantity < 6) theme.bg3 else theme.bg2)
                        .clickable { if (quantity < 6) quantity++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = if (quantity < 6) theme.t1 else theme.t4)
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.End) {
                    Text("TOTAL", style = MaterialTheme.typography.labelSmall, color = theme.t4)
                    Text("₹${(selectedQuote!!.price * quantity).toInt()}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConfirm(selectedQuote!!, quantity) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (theme.isDark) Color.White else Color.Black,
                    contentColor = if (theme.isDark) Color.Black else Color.White
                )
            ) {
                Text("CONFIRM ₹${(selectedQuote!!.price * quantity).toInt()}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
