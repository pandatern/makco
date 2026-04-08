package com.pandatern.makco.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.ui.theme.*

enum class BottomTab { HOME, TICKETS, PROFILE }

@Composable
fun BottomNavBar(selectedTab: BottomTab, onTabSelected: (BottomTab) -> Unit) {
    val theme = LocalThemeManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(if (theme.isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabItem(
                icon = "🚇",
                label = "Home",
                isSelected = selectedTab == BottomTab.HOME,
                onClick = { onTabSelected(BottomTab.HOME) },
                theme = theme
            )
            TabItem(
                icon = "🎫",
                label = "Tickets",
                isSelected = selectedTab == BottomTab.TICKETS,
                onClick = { onTabSelected(BottomTab.TICKETS) },
                theme = theme
            )
            TabItem(
                icon = "👤",
                label = "Profile",
                isSelected = selectedTab == BottomTab.PROFILE,
                onClick = { onTabSelected(BottomTab.PROFILE) },
                theme = theme
            )
        }
    }
}

@Composable
fun TabItem(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit, theme: ThemeManager) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) theme.highlight else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) theme.t1 else theme.t4
        )
    }
}
