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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.local.CacheManager
import com.pandatern.makco.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    stations: List<Station>,
    selectedSource: Station?,
    selectedDestination: Station?,
    onStationClick: (isSource: Boolean) -> Unit,
    onSearchClick: () -> Unit
) {
    val theme = LocalThemeManager.current
    val ctx = LocalContext.current
    var recentStations by remember { mutableStateOf(CacheManager.getRecentStations(ctx)) }

    LaunchedEffect(selectedSource, selectedDestination) {
        recentStations = CacheManager.getRecentStations(ctx)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Animated header
        var headerVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { headerVisible = true }

        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = {-30})
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.ic_location), contentDescription = "M", colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("MAKCO", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = theme.t1)
                    Text("CHENNAI METRO", style = MaterialTheme.typography.labelMedium, color = theme.t3)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Station selectors with animation
        var selectorsVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { selectorsVisible = true }

        AnimatedVisibility(
            visible = selectorsVisible,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) + slideInVertically(initialOffsetY = {30})
        ) {
            Column {
                StationSelector(
                    label = "FROM",
                    icon = R.drawable.ic_location,
                    station = selectedSource,
                    onClick = { onStationClick(true) },
                    theme = theme
                )

                Spacer(modifier = Modifier.height(8.dp))

                StationSelector(
                    label = "TO",
                    icon = R.drawable.ic_location,
                    station = selectedDestination,
                    onClick = { onStationClick(false) },
                    theme = theme
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search button with scale animation
        val isReady = selectedSource != null && selectedDestination != null

        var buttonScale by remember { mutableFloatStateOf(1f) }
        LaunchedEffect(isReady) {
            buttonScale = 1f
        }

        Button(
            onClick = {
                // Scale animation on click
                buttonScale = 0.95f
                onSearchClick()
            },
            enabled = isReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.t1,
                contentColor = theme.bg,
                disabledContainerColor = theme.bg3,
                disabledContentColor = theme.t4
            ),
            shape = RoundedCornerShape(0.dp)
        ) {
            AnimatedContent(
                targetState = isReady,
                transitionSpec = {
                    fadeIn(tween(200)) with fadeOut(tween(200))
                },
                label = "button_text"
            ) { ready ->
                Text(
                    if (ready) "SEARCH" else "SELECT STATIONS",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent stations with stagger animation
        if (recentStations.isNotEmpty()) {
            var recentVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { recentVisible = true }

            AnimatedVisibility(visible = recentVisible, enter = fadeIn(tween(300, delayMillis = 200))) {
                Column {
                    Text("RECENT", style = MaterialTheme.typography.labelMedium, color = theme.t3)
                    Spacer(modifier = Modifier.height(12.dp))

                    recentStations.take(3).forEachIndexed { index, station ->
                        var itemVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { itemVisible = true }

                        AnimatedVisibility(
                            visible = itemVisible,
                            enter = fadeIn(tween(200, delayMillis = 300 + index * 50)) + slideInHorizontally(initialOffsetX = {20})
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(0.dp))
                                    .border(1.dp, theme.outline)
                                    .clickable { onStationClick(true) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(painter = painterResource(R.drawable.ic_location), contentDescription = null, colorFilter = ColorFilter.tint(theme.t3), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(station.name, style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text("${stations.size} STATIONS", style = MaterialTheme.typography.labelSmall, color = theme.t4, modifier = Modifier.padding(bottom = 100.dp))
    }
}

@Composable
fun StationSelector(label: String, icon: Int, station: Station?, onClick: () -> Unit, theme: ThemeManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
            .border(2.dp, if (station != null) theme.t1 else theme.outline)
            .clickable(onClick = onClick)
            .background(theme.bg2)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(icon), contentDescription = null, colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = theme.t3)
            Text(text = station?.name ?: "Select station", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (station != null) FontWeight.Bold else FontWeight.Normal), color = if (station != null) theme.t1 else theme.t4)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("→", color = theme.t3, style = MaterialTheme.typography.titleMedium)
    }
}