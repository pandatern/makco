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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 4.sp),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .border(3.dp, theme.t1, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.t1, strokeWidth = 3.dp)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(6.dp, RoundedCornerShape(36.dp))
                            .clip(RoundedCornerShape(36.dp))
                            .background(theme.t1),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (profile?.firstName?.firstOrNull() ?: profile?.maskedMobileNumber?.first() ?: '?').toString(),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                            color = if (theme.isDark) Black else White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = profile?.maskedMobileNumber ?: "---",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = theme.t1
                    )
                    profile?.firstName?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(it, style = MaterialTheme.typography.bodyLarge, color = theme.t2)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("APPEARANCE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
                .clickable { onThemeToggle() }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (theme.isDark) "🌙" else "☀️", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("THEME", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                }
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(32.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.t1),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (theme.isDark) "DARK" else "LIGHT",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (theme.isDark) Black else White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("MENU", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.t3)
        Spacer(modifier = Modifier.height(12.dp))

        MenuItem(
            icon = R.drawable.ic_ticket,
            text = "My Tickets",
            theme = theme,
            onClick = onTicketsClick
        )
        
        MenuItem(
            icon = R.drawable.ic_location,
            text = "Recent Stations",
            theme = theme,
            onClick = onStationsClick
        )
        
        MenuItem(
            icon = R.drawable.ic_profile,
            text = "Account Settings",
            theme = theme,
            onClick = onSettingsClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(theme.bg2)
                .border(3.dp, theme.t1, RoundedCornerShape(16.dp))
                .clickable { onLogout() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "LOGOUT",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = theme.t1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("MAKCO v1.0.0", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = theme.t4)
        Text("BUILT BY PANDATERN", style = MaterialTheme.typography.labelSmall, color = theme.t4)
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun MenuItem(
    icon: Int,
    text: String,
    theme: ThemeManager,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(theme.bg2)
            .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(theme.t1),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
            Spacer(modifier = Modifier.weight(1f))
            Text("→", style = MaterialTheme.typography.titleLarge, color = theme.t3)
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}