package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    token: String,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit
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
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black
            ),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = theme.t1, strokeWidth = 2.dp)
            }
        } else {
            // Phone
            profile?.let { p ->
                Text(
                    text = p.maskedMobileNumber,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = theme.t1
                )

                if (p.firstName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = p.firstName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = theme.t2
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Theme toggle
            Text(
                text = "APPEARANCE",
                style = MaterialTheme.typography.labelMedium,
                color = theme.t4
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeToggle() }
                    .background(theme.bg2)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "THEME",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = theme.t2
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (theme.isDark) "DARK" else "LIGHT",
                        style = MaterialTheme.typography.labelMedium,
                        color = theme.t3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(22.dp)
                            .background(
                                if (theme.isDark) MetroBlue else theme.bg4,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(11.dp)
                            ),
                        contentAlignment = if (theme.isDark) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(3.dp)
                                .size(16.dp)
                                .background(
                                    if (theme.isDark) MaterialTheme.colorScheme.background else theme.t1,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info
            Text(
                text = "INFO",
                style = MaterialTheme.typography.labelMedium,
                color = theme.t4
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileItem("VERSION", "1.0.0", theme)
            ProfileItem("NETWORK", "CHENNAI METRO", theme)
            ProfileItem("LINES", "BLUE & GREEN", theme)

            Spacer(modifier = Modifier.weight(1f))

            // Logout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onLogout) {
                    Text(
                        text = "LOGOUT",
                        style = MaterialTheme.typography.labelLarge,
                        color = Error
                    )
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MAKCO v1.0.0",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = theme.t4
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BUILT BY PANDATERN",
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.t4
                )
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String, theme: ThemeManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = theme.t4
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = theme.t2
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(theme.divider)
    )
}
