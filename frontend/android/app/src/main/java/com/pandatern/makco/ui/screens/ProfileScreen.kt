package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
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
    onLogout: () -> Unit
) {
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
            .background(Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black
            ),
            color = Text1
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = White, strokeWidth = 2.dp)
            }
        } else {
            // Phone
            profile?.let { p ->
                Text(
                    text = p.maskedMobileNumber,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Text1
                )

                if (p.firstName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = p.firstName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Text2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (p.hasTakenRide) "HAS TRAVELLED" : "NO RIDES YET",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (p.hasTakenRide) Success else Text4
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Info
            Text(
                text = "INFO",
                style = MaterialTheme.typography.labelMedium,
                color = Text4
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileItem("VERSION", "1.0.0")
            ProfileItem("NETWORK", "CHENNAI METRO")
            ProfileItem("LINES", "BLUE & GREEN")
            ProfileItem("STATIONS", "41")

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
                    text = "MAKCO",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Text3
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BUILT BY PANDATERN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Text4
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "MADE FOR CMRL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Text4
                )
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
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
            color = Text4
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Text2
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Dark4)
    )
}
