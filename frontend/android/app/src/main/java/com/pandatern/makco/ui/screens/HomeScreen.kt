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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.local.CacheManager
import com.pandatern.makco.ui.theme.*

@Composable
fun HomeScreen(
    stations: List<Station>,
    selectedSource: Station?,
    selectedDestination: Station?,
    onStationClick: (isSource: Boolean) -> Unit,
    onSearchClick: () -> Unit
) {
    val theme = LocalThemeManager.current
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var recentStations by remember { mutableStateOf(CacheManager.getRecentStations(ctx)) }
    var animatedSource by remember { mutableStateOf(false) }
    var animatedDest by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSource, selectedDestination) {
        recentStations = CacheManager.getRecentStations(ctx)
        if (selectedSource != null) animatedSource = true
        if (selectedDestination != null) animatedDest = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header with Metro icon
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Metro icon badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MetroBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "MAKCO",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                    color = theme.t1
                )
                Text("CHENNAI METRO", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Metro card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .padding(20.dp)
        ) {
            Column {
                Text("PLAN YOUR JOURNEY", style = MaterialTheme.typography.labelMedium, color = theme.t4)

                Spacer(modifier = Modifier.height(20.dp))

                // From
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    StationSelector(
                        label = "FROM",
                        station = selectedSource,
                        dotColor = MetroGreen,
                        isAnimated = animatedSource,
                        onClick = { onStationClick(true) },
                        theme = theme
                    )
                }

                // Divider with animation
                AnimatedVisibility(
                    visible = selectedSource != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .width(2.dp)
                            .height(16.dp)
                            .background(if (theme.isDark) Color(0xFF333333) else Color(0xFFDDDDDD))
                    )
                }

                // To
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    StationSelector(
                        label = "TO",
                        station = selectedDestination,
                        dotColor = MetroBlue,
                        isAnimated = animatedDest,
                        onClick = { onStationClick(false) },
                        theme = theme
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search button
        val isReady = selectedSource != null && selectedDestination != null
        val searchAlpha by animateFloatAsState(
            targetValue = if (isReady) 1f else 0.5f,
            label = "searchAlpha"
        )

        Button(
            onClick = onSearchClick,
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .alpha(searchAlpha),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (theme.isDark) Color(0xFF1A1A1A) else Color(0xFFE0E0E0),
                contentColor = theme.t1,
                disabledContainerColor = theme.bg2,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("SEARCH FARES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent stations
        if (recentStations.isNotEmpty()) {
            Text("RECENT", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(12.dp))

            recentStations.take(3).forEach { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onStationClick(true) }
                        .background(if (theme.isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MetroBlue)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(station.name, style = MaterialTheme.typography.bodyMedium, color = theme.t3)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Lines indicator with styled badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blue line badge
            Box(
                modifier = Modifier
                    .background(MetroBlue.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(MetroBlue))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("BLUE LINE", style = MaterialTheme.typography.labelSmall, color = MetroBlue)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Green line badge
            Box(
                modifier = Modifier
                    .background(MetroGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(MetroGreen))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("GREEN LINE", style = MaterialTheme.typography.labelSmall, color = MetroGreen)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("${stations.size} stations", style = MaterialTheme.typography.labelSmall, color = theme.t4)
        }
    }
}

@Composable
fun StationSelector(
    label: String,
    station: Station?,
    dotColor: Color,
    isAnimated: Boolean,
    onClick: () -> Unit,
    theme: ThemeManager
) {
    val bgColor by animateColorAsState(
        targetValue = if (isAnimated && station != null) {
            if (theme.isDark) Color(0xFF111111) else Color(0xFFE8E8E8)
        } else {
            if (theme.isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5)
        },
        label = "bgColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (station != null) {
                    if (theme.isDark) Color(0xFF333333) else Color(0xFFDDDDDD)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .background(bgColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Animated dot
        val dotSize by animateDpAsState(
            targetValue = if (station != null) 12.dp else 8.dp,
            label = "dotSize"
        )

        Box(
            modifier = Modifier
                .size(dotSize)
                .background(dotColor)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = theme.t4
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = station?.name ?: "Select station",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (station != null) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (station != null) theme.t1 else theme.t4
            )
        }

        Text(
            text = "→",
            style = MaterialTheme.typography.titleMedium,
            color = if (station != null) theme.t2 else theme.t4
        )
    }
}
