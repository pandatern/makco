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

enum class BottomTab {
    HOME, TICKETS, PROFILE
}

@Composable
fun BottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    val theme = LocalThemeManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (theme.isDark) Color(0x40FFFFFF) else Color(0x40000000)
                )
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BottomTabItem(
                label = "HOME",
                isSelected = selectedTab == BottomTab.HOME,
                onClick = { onTabSelected(BottomTab.HOME) },
                theme = theme
            )
            BottomTabItem(
                label = "TICKETS",
                isSelected = selectedTab == BottomTab.TICKETS,
                onClick = { onTabSelected(BottomTab.TICKETS) },
                theme = theme
            )
            BottomTabItem(
                label = "PROFILE",
                isSelected = selectedTab == BottomTab.PROFILE,
                onClick = { onTabSelected(BottomTab.PROFILE) },
                theme = theme
            )
        }
    }
}

@Composable
fun BottomTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    theme: ThemeManager
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    if (theme.isDark) Color(0x60FFFFFF) else Color(0x60000000)
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) {
                if (theme.isDark) Color.White else Color.Black
            } else {
                if (theme.isDark) Color(0xFF888888) else Color(0xFF666666)
            }
        )
    }
}
