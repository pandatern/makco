package com.pandatern.makco.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.ui.theme.*

enum class BottomTab { HOME, TICKETS, PROFILE }

@OptIn(ExperimentalAnimationApi::class)
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
            TabItem(icon = R.drawable.ic_home, label = "HOME", isSelected = selectedTab == BottomTab.HOME, onClick = { onTabSelected(BottomTab.HOME) }, theme = theme)
            TabItem(icon = R.drawable.ic_ticket, label = "TICKETS", isSelected = selectedTab == BottomTab.TICKETS, onClick = { onTabSelected(BottomTab.TICKETS) }, theme = theme)
            TabItem(icon = R.drawable.ic_profile, label = "PROFILE", isSelected = selectedTab == BottomTab.PROFILE, onClick = { onTabSelected(BottomTab.PROFILE) }, theme = theme)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TabItem(icon: Int, label: String, isSelected: Boolean, onClick: () -> Unit, theme: ThemeManager) {
    // Animate scale on selection
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tab_scale"
    )

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(if (isSelected) theme.t1 else theme.bg2)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = label,
            colorFilter = ColorFilter.tint(if (isSelected) theme.bg else theme.t3),
            modifier = Modifier
                .size(20.dp)
                .scale(scale)
        )
        Spacer(modifier = Modifier.width(8.dp))
        AnimatedContent(
            targetState = isSelected,
            transitionSpec = {
                fadeIn(tween(150)) with fadeOut(tween(150))
            },
            label = "tab_label"
        ) { selected ->
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) theme.bg else theme.t3
            )
        }
    }
}