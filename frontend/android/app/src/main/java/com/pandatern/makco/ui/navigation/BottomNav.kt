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
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(
                    // Glass effect - semi-transparent
                    if (theme.isDark) Color(0x33FFFFFF) else Color(0x33000000)
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            BottomTabItem(
                label = "HOME",
                isSelected = selectedTab == BottomTab.HOME,
                onClick = { onTabSelected(BottomTab.HOME) },
                isDark = theme.isDark
            )
            BottomTabItem(
                label = "TICKETS",
                isSelected = selectedTab == BottomTab.TICKETS,
                onClick = { onTabSelected(BottomTab.TICKETS) },
                isDark = theme.isDark
            )
            BottomTabItem(
                label = "PROFILE",
                isSelected = selectedTab == BottomTab.PROFILE,
                onClick = { onTabSelected(BottomTab.PROFILE) },
                isDark = theme.isDark
            )
        }
    }
}

@Composable
fun BottomTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                when {
                    isSelected && isDark -> Color(0x40FFFFFF)
                    isSelected && !isDark -> Color(0x40000000)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = when {
                isSelected && isDark -> Color.White
                isSelected && !isDark -> Color.Black
                isDark -> Color(0xFF888888)
                else -> Color(0xFF888888)
            }
        )
    }
}
