package com.pandatern.makco.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val context = LocalContext.current
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
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Menu items
            ProfileMenuItem(
                label = "My Tickets",
                onClick = { /* handled by nav */ }
            )

            ProfileMenuItem(
                label = "Support",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pandatern.tech"))
                    context.startActivity(intent)
                }
            )

            ProfileMenuItem(
                label = "Terms & Conditions",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pandatern.tech/terms"))
                    context.startActivity(intent)
                }
            )

            ProfileMenuItem(
                label = "Privacy Policy",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pandatern.tech/privacy"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LOGOUT",
                    style = MaterialTheme.typography.labelLarge,
                    color = Error
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Text2
        )
        Text(
            text = "→",
            style = MaterialTheme.typography.titleMedium,
            color = Text4
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Dark4)
    )
}
