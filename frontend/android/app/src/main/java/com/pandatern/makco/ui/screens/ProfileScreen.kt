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
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    token: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = ApiClient.instance.getProfile(token)
                if (resp.isSuccessful) {
                    profile = resp.body()
                }
            } catch (e: Exception) {
                error = e.message
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(start = 12.dp)) {
            TextButton(onClick = onBack) {
                Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.labelMedium,
            color = Gray3,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                }
            }
            profile != null -> {
                val p = profile!!

                // User info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = p.maskedMobileNumber,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = White
                    )

                    if (p.firstName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = p.firstName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Stats
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "STATS",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gray3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileStatRow("RIDES", if (p.hasTakenRide) "YES" else "NONE")
                    ProfileStatRow("STATUS", if (p.isBlocked) "BLOCKED" else "ACTIVE")
                    ProfileStatRow("REFERRAL", p.customerReferralCode)
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Logout
                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "LOGOUT",
                        style = MaterialTheme.typography.labelLarge,
                        color = Error
                    )
                }
            }
            else -> {
                Text(
                    text = "Could not load profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray2,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Gray2
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Dark4)
    )
}
