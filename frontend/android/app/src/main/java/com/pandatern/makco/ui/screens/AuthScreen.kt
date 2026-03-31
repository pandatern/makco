package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: (token: String) -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var authId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var otpSent by remember { mutableStateOf(false) }
    var attemptsLeft by remember { mutableStateOf(3) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Makco",
            style = MaterialTheme.typography.displayLarge.copy(
                color = Accent,
                fontWeight = FontWeight.Black
            )
        )

        Text(
            text = "Chennai Metro",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(64.dp))

        if (!otpSent) {
            Text(
                text = "Enter Phone Number",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                        phone = it
                        error = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("9876543210", color = TextMuted) },
                prefix = { Text("+91 ", color = TextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Divider,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Accent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (phone.length != 10) {
                        error = "Enter 10 digit phone number"
                        return@Button
                    }
                    isLoading = true
                    error = null
                    scope.launch {
                        try {
                            val resp = ApiClient.instance.initiateAuth(
                                AuthRequest(mobileNumber = phone)
                            )
                            if (resp.isSuccessful && resp.body() != null) {
                                val body = resp.body()!!
                                authId = body.authId
                                attemptsLeft = body.attempts
                                otpSent = true
                            } else {
                                error = "Auth failed: ${resp.code()}"
                            }
                        } catch (e: Exception) {
                            error = "Network error: ${e.message}"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Background,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Send OTP",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Background,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        } else {
            Text(
                text = "Enter OTP",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Text(
                text = "Sent to +91 $phone ($attemptsLeft attempts left)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                        otp = it
                        error = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("4-digit OTP", color = TextMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Divider,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Accent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (otp.length != 4) {
                        error = "OTP must be 4 digits"
                        return@Button
                    }
                    isLoading = true
                    error = null
                    scope.launch {
                        try {
                            val resp = ApiClient.instance.verifyAuth(
                                authId!!,
                                VerifyRequest(otp = otp)
                            )
                            if (resp.isSuccessful && resp.body() != null) {
                                val body = resp.body()!!
                                onAuthSuccess(body.token)
                            } else {
                                error = "Wrong OTP"
                                attemptsLeft--
                                if (attemptsLeft <= 0) {
                                    otpSent = false
                                    otp = ""
                                    authId = null
                                    error = "No attempts left. Try again."
                                }
                            }
                        } catch (e: Exception) {
                            error = "Network error: ${e.message}"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Background,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Verify OTP",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Background,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    otpSent = false
                    otp = ""
                    authId = null
                    error = null
                }
            ) {
                Text("Change number", color = TextSecondary)
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = Error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
