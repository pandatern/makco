package com.pandatern.makco.ui.screens

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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
            }
        } else {
            profile?.let { p ->
                Text(
                    text = p.maskedMobileNumber,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = theme.t1
                )
                if (p.firstName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(p.firstName, style = MaterialTheme.typography.bodyLarge, color = theme.t2)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Theme Toggle - Animated
        Text("APPEARANCE", style = MaterialTheme.typography.labelMedium, color = theme.t4)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onThemeToggle() }
                .background(theme.bg2)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("THEME", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = theme.t2)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (theme.isDark) "DARK" else "LIGHT",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.t3
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
                            .background(theme.t1)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu items with icons
        MenuItem(icon = R.drawable.ic_ticket, text = "My Tickets", theme = theme, onClick = onTicketsClick)
        MenuItem(icon = R.drawable.ic_location, text = "Recent Stations", theme = theme, onClick = onStationsClick)
        MenuItem(icon = R.drawable.ic_profile, text = "Account Settings", theme = theme, onClick = onSettingsClick)

        Spacer(modifier = Modifier.weight(1f))

        // Logout button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, theme.outline)
                .clickable { onLogout() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("LOGOUT", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer
        Text("MAKCO v1.0.0", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t4, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text("BUILT BY PANDATERN", style = MaterialTheme.typography.labelSmall, color = theme.t4, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun MenuItem(icon: Int, text: String, theme: ThemeManager, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(theme.bg2)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(icon), contentDescription = null, colorFilter = ColorFilter.tint(theme.t2), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = theme.t2)
        Spacer(modifier = Modifier.weight(1f))
        Text("→", color = theme.t3)
    }
    Spacer(modifier = Modifier.height(8.dp))
}