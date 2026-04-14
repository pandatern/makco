package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.data.model.AuthRequest
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.LocalThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Composable
fun DebugScreen(
    onRetry: () -> Unit
) {
    val theme = LocalThemeManager.current
    val scope = rememberCoroutineScope()
    var testResult by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val resp = ApiClient.instance.initiateAuth(AuthRequest("9123456789"))
                    testResult = if (resp.isSuccessful) {
                        "SUCCESS!\nauthId: ${resp.body()?.authId ?: "none"}"
                    } else {
                        "HTTP ${resp.code()}: ${resp.message()}\n${resp.errorBody()?.string() ?: ""}"
                    }
                }
            } catch (e: SocketTimeoutException) {
                testResult = "TIMEOUT: Server took too long to respond"
            } catch (e: UnknownHostException) {
                testResult = "DNS ERROR: Cannot find api.pandatern.tech\n${e.message}"
            } catch (e: Exception) {
                testResult = "${e.javaClass.simpleName}:\n${e.message ?: "unknown error"}\n\nLocalized: ${e.localizedMessage ?: "none"}"
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "MAKCO",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "DEBUG MODE",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = theme.t3
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(2.dp, theme.t1, RoundedCornerShape(12.dp))
                .background(theme.bg2)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Text(
                    "API Test Result:",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.t3
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoading) {
                    Text("Testing API...", style = MaterialTheme.typography.bodyMedium, color = theme.t2)
                } else {
                    Text(
                        testResult ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = if (testResult?.startsWith("SUCCESS") == true) theme.t1 else theme.err
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "URL: https://api.pandatern.tech/auth",
            style = MaterialTheme.typography.bodySmall,
            color = theme.t4
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("RETRY", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Try on different network (WiFi/Mobile)",
            style = MaterialTheme.typography.bodySmall,
            color = theme.t4
        )
    }
}
