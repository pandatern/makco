package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: (token: String) -> Unit
) {
    val theme = LocalThemeManager.current

    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var authId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var otpSent by remember { mutableStateOf(false) }
    var attemptsLeft by remember { mutableStateOf(3) }

    val scope = rememberCoroutineScope()
    val ctx = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(160.dp))

        // Logo
        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "CHENNAI METRO",
            style = MaterialTheme.typography.labelMedium,
            color = theme.t4
        )

        Spacer(modifier = Modifier.height(64.dp))

        if (!otpSent) {
            Text("ENTER YOUR PHONE", style = MaterialTheme.typography.labelMedium, color = theme.t3)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(theme.bg2)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("+91", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium), color = theme.t3)
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) { phone = it; error = null } },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("9876543210", color = theme.t4) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = theme.t1, unfocusedTextColor = theme.t1, cursorColor = theme.t3
                    ),
                    textStyle = MaterialTheme.typography.titleLarge,
                    singleLine = true, enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (phone.length != 10) { error = "ENTER 10 DIGITS"; return@Button }
                    isLoading = true; error = null
                    scope.launch {
                        try {
                            val resp = ApiClient.instance.initiateAuth(AuthRequest(mobileNumber = phone))
                            if (resp.isSuccessful && resp.body() != null) {
                                authId = resp.body()!!.authId; attemptsLeft = resp.body()!!.attempts; otpSent = true
                                com.pandatern.makco.data.local.TokenManager.savePhone(ctx, phone)
                            } else {
                                error = when (resp.code()) {
                                    502, 503, 504 -> "SERVER DOWN"
                                    429 -> "TOO MANY REQUESTS"
                                    else -> "FAILED (${resp.code()})"
                                }
                            }
                        } catch (e: Exception) {
                            error = when {
                                e.message?.contains("timeout") == true -> "TIMEOUT"
                                e.message?.contains("Unable to resolve") == true -> "NO INTERNET"
                                else -> "NETWORK ERROR"
                            }
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (theme.isDark) Color.White else Color.Black,
                    contentColor = if (theme.isDark) Color.Black else Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = theme.bg, strokeWidth = 2.dp)
                else Text("CONTINUE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        } else {
            Text("ENTER OTP", style = MaterialTheme.typography.labelMedium, color = theme.t3)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Sent to +91 $phone", style = MaterialTheme.typography.bodyMedium, color = theme.t4)
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().background(theme.bg2).padding(horizontal = 20.dp, vertical = 4.dp)) {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) { otp = it; error = null } },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("4 DIGITS", color = theme.t4) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = theme.t1, unfocusedTextColor = theme.t1, cursorColor = theme.t3
                    ),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    singleLine = true, enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("$attemptsLeft ATTEMPTS REMAINING", style = MaterialTheme.typography.labelSmall,
                color = if (attemptsLeft <= 1) Error else theme.t4)

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (otp.length != 4) { error = "OTP MUST BE 4 DIGITS"; return@Button }
                    isLoading = true; error = null
                    scope.launch {
                        try {
                            val resp = ApiClient.instance.verifyAuth(authId!!, VerifyRequest(otp = otp))
                            if (resp.isSuccessful && resp.body() != null) { onAuthSuccess(resp.body()!!.token) }
                            else {
                                when (resp.code()) {
                                    502, 503, 504 -> { error = "SERVER DOWN" }
                                    400 -> {
                                        error = "WRONG OTP"; attemptsLeft--
                                        if (attemptsLeft <= 0) { otpSent = false; otp = ""; authId = null; error = "NO ATTEMPTS LEFT" }
                                    }
                                    else -> { error = "FAILED (${resp.code()})" }
                                }
                            }
                        } catch (e: Exception) {
                            error = when {
                                e.message?.contains("timeout") == true -> "TIMEOUT"
                                e.message?.contains("Unable to resolve") == true -> "NO INTERNET"
                                else -> "NETWORK ERROR"
                            }
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (theme.isDark) Color.White else Color.Black,
                    contentColor = if (theme.isDark) Color.Black else Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = theme.bg, strokeWidth = 2.dp)
                else Text("VERIFY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { otpSent = false; otp = ""; authId = null; error = null }) {
                Text("WRONG NUMBER?", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            }
        }

        // Error
        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().background(Error.copy(alpha = 0.1f)).padding(14.dp)) {
                Text(it, color = Error, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
