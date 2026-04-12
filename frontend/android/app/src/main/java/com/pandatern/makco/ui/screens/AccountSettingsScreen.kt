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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.ui.theme.*

@Composable
fun AccountSettingsScreen(
    onBack: () -> Unit
) {
    val theme = LocalThemeManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Text("←", style = MaterialTheme.typography.headlineMedium, color = theme.t1)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "ACCOUNT SETTINGS",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = theme.t1
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Settings sections
        SettingsSection(title = "ACCOUNT") {
            SettingsItem(
                icon = R.drawable.ic_profile,
                title = "Personal Details",
                subtitle = "Name, phone number, email",
                theme = theme,
                onClick = { }
            )
            SettingsItem(
                icon = R.drawable.ic_ticket,
                title = "Saved Payment Methods",
                subtitle = "UPI, cards, wallets",
                theme = theme,
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "APP") {
            SettingsItem(
                icon = R.drawable.ic_location,
                title = "Language",
                subtitle = "English",
                theme = theme,
                onClick = { }
            )
            SettingsItem(
                icon = R.drawable.ic_profile,
                title = "Notifications",
                subtitle = "Push notifications, alerts",
                theme = theme,
                onClick = { }
            )
            SettingsItem(
                icon = R.drawable.ic_ticket,
                title = "Privacy Policy",
                subtitle = "View our privacy policy",
                theme = theme,
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "SUPPORT") {
            SettingsItem(
                icon = R.drawable.ic_location,
                title = "Help & FAQ",
                subtitle = "Get help with common issues",
                theme = theme,
                onClick = { }
            )
            SettingsItem(
                icon = R.drawable.ic_profile,
                title = "Contact Us",
                subtitle = "Reach our support team",
                theme = theme,
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            "MAKCO v1.0.0",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = theme.t4,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "© 2024 Pandatern",
            style = MaterialTheme.typography.labelSmall,
            color = theme.t4,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val theme = LocalThemeManager.current
    Text(title, style = MaterialTheme.typography.labelMedium, color = theme.t4)
    Spacer(modifier = Modifier.height(12.dp))
    Column(content = content)
}

@Composable
private fun SettingsItem(
    icon: Int,
    title: String,
    subtitle: String,
    theme: ThemeManager,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(theme.bg2)
            .border(1.dp, theme.outline, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(theme.t2),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = theme.t1)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = theme.t3)
        }
        Text("→", color = theme.t3)
    }
    Spacer(modifier = Modifier.height(8.dp))
}