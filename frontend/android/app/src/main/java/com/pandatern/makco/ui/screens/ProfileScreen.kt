package com.pandatern.makco.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    token: String,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit,
    onTicketsClick: () -> Unit = {},
    onStationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val theme = LocalThemeManager.current
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Animate profile card
    val cardAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500),
        label = "cardAlpha"
    )

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = ApiClient.instance.getProfile(token)
                if (resp.isSuccessful) profile = resp.body()
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Profile card with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(theme.actionSubtle, theme.bg2)
                    )
                )
                .border(2.dp, theme.outline, RoundedCornerShape(16.dp))
                .padding(24.dp)
                .graphicsLayer { alpha = cardAlpha }
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
                }
            } else {
                profile?.let { p ->
                    Column {
                        // Avatar placeholder
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(theme.action),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                (p.firstName?.firstOrNull() ?: p.maskedMobileNumber.first()).toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = theme.bg
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = p.maskedMobileNumber,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = theme.t1
                        )
                        p.firstName?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(it, style = MaterialTheme.typography.bodyLarge, color = theme.t2)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Theme toggle with action color
        Text("APPEARANCE", style = MaterialTheme.typography.labelMedium, color = theme.t4)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onThemeToggle() }
                .background(theme.bg2)
                .border(1.dp, theme.outline, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(theme.action),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("THEME", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = theme.t2)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (theme.isDark) "DARK" else "LIGHT",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.action
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Toggle indicator
                Box(
                    modifier = Modifier
                        .size(48.dp, 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(theme.bg3),
                    contentAlignment = if (theme.isDark) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.action)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu items with real click handlers
        Text("MENU", style = MaterialTheme.typography.labelMedium, color = theme.t4)
        Spacer(modifier = Modifier.height(12.dp))

        // My Tickets - goes to tickets tab
        MenuItem(
            icon = R.drawable.ic_ticket,
            text = "My Tickets",
            theme = theme,
            onClick = onTicketsClick,
            actionColor = theme.action
        )
        
        // Recent Stations
        MenuItem(
            icon = R.drawable.ic_location,
            text = "Recent Stations",
            theme = theme,
            onClick = onStationsClick,
            actionColor = theme.action
        )
        
        // Account Settings
        MenuItem(
            icon = R.drawable.ic_profile,
            text = "Account Settings",
            theme = theme,
            onClick = onSettingsClick,
            actionColor = theme.action
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout button with action color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, theme.action, RoundedCornerShape(12.dp))
                .clickable { onLogout() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "LOGOUT",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = theme.action
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer
        Text(
            "MAKCO v1.0.0",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = theme.t4,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            "BUILT BY PANDATERN",
            style = MaterialTheme.typography.labelSmall,
            color = theme.t4,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun MenuItem(
    icon: Int,
    text: String,
    theme: ThemeManager,
    onClick: () -> Unit,
    actionColor: Color = theme.t2
) {
    // Animate on press
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "menuScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(theme.bg2)
            .border(1.dp, theme.outline, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(actionColor),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = theme.t2)
        Spacer(modifier = Modifier.weight(1f))
        Text("→", color = theme.t3)
    }
    Spacer(modifier = Modifier.height(12.dp))
}