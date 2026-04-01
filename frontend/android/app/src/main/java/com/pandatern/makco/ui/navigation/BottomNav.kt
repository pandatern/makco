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

    // Outer padding to float above bottom
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(
                    // Always visible glass - works in both themes
                    if (theme.isDark) Color(0x44FFFFFF) else Color(0x99FFFFFF)
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabItem("HOME", selectedTab == BottomTab.HOME, { onTabSelected(BottomTab.HOME) }, theme)
            TabItem("TICKETS", selectedTab == BottomTab.TICKETS, { onTabSelected(BottomTab.TICKETS) }, theme)
            TabItem("PROFILE", selectedTab == BottomTab.PROFILE, { onTabSelected(BottomTab.PROFILE) }, theme)
        }
    }
}

@Composable
fun TabItem(label: String, isSelected: Boolean, onClick: () -> Unit, theme: ThemeManager) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (isSelected) {
                    if (theme.isDark) Color(0x60FFFFFF) else Color(0x60000000)
                } else {
                    Color.Transparent
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
                isSelected && theme.isDark -> Color.White
                isSelected && !theme.isDark -> Color.Black
                theme.isDark -> Color(0xFF888888)
                else -> Color(0xFF666666)
            }
        )
    }
}
