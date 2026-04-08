package com.pandatern.makco.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
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
                .background(if (theme.isDark) theme.bg2 else theme.bg2),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TabItem(icon = com.pandatern.makco.R.drawable.ic_home, label = "HOME", isSelected = selectedTab == BottomTab.HOME, onClick = { onTabSelected(BottomTab.HOME) }, theme = theme)
            TabItem(icon = com.pandatern.makco.R.drawable.ic_ticket, label = "TICKETS", isSelected = selectedTab == BottomTab.TICKETS, onClick = { onTabSelected(BottomTab.TICKETS) }, theme = theme)
            TabItem(icon = com.pandatern.makco.R.drawable.ic_profile, label = "PROFILE", isSelected = selectedTab == BottomTab.PROFILE, onClick = { onTabSelected(BottomTab.PROFILE) }, theme = theme)
        }
    }
}

@Composable
fun TabItem(icon: Int, label: String, isSelected: Boolean, onClick: () -> Unit, theme: ThemeManager) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(if (isSelected) theme.t1 else theme.bg2)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(icon), contentDescription = label, colorFilter = ColorFilter.tint(if (isSelected) theme.bg else theme.t3), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = if (isSelected) theme.bg else theme.t3)
    }
}