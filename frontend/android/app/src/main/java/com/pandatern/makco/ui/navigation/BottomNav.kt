package com.pandatern.makco.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(0.dp))
                .background(if (theme.isDark) Color(0xFF111111) else Color(0xFFEEEEEE)),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TabItem(
                label = "HOME",
                isSelected = selectedTab == BottomTab.HOME,
                onClick = { onTabSelected(BottomTab.HOME) },
                theme = theme
            )
            TabItem(
                label = "TICKETS",
                isSelected = selectedTab == BottomTab.TICKETS,
                onClick = { onTabSelected(BottomTab.TICKETS) },
                theme = theme
            )
            TabItem(
                label = "PROFILE",
                isSelected = selectedTab == BottomTab.PROFILE,
                onClick = { onTabSelected(BottomTab.PROFILE) },
                theme = theme
            )
        }
    }
}

@Composable
fun TabItem(label: String, isSelected: Boolean, onClick: () -> Unit, theme: ThemeManager) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .background(if (isSelected) theme.t1 else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (isSelected) theme.bg else theme.t3
        )
    }
}
