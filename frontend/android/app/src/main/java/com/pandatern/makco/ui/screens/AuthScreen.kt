package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var authId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var otpSent by remember { mutableStateOf(false) }
    var attemptsLeft by remember { mutableStateOf(3) }

    val scope = rememberCoroutineScope()

    // Subtle breathing animation for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "breath")
    val logoGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Logo with subtle glow
            Text(
                text = "MAKCO",
                style = MaterialTheme.typography.displayLarge,
                color = White.copy(alpha = logoGlow)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "CHENNAI METRO",
                style = MaterialTheme.typography.labelMedium,
                color = Text3
            )

            Spacer(modifier = Modifier.weight(0.2f))

            if (!otpSent) {
                // Phone step
                Text(
                    text = "ENTER YOUR PHONE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Text3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Phone field with country code integrated
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Dark3)
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "+91",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Text2
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                    phone = it
                                    error = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "9876543210",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Dark5
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                cursorColor = Text3
                            ),
                            textStyle = MaterialTheme.typography.titleLarge,
                            singleLine = true,
                            enabled = !isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (phone.length != 10) {
                            error = "ENTER 10 DIGITS"
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
                                    authId = resp.body()!!.authId
                                    attemptsLeft = resp.body()!!.attempts
                                    otpSent = true
                                } else {
                                    error = "FAILED"
                                }
                            } catch (e: Exception) {
                                error = "ERR: ${e.message ?: e.javaClass.simpleName}"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "CONTINUE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            } else {
                // OTP step
                Text(
                    text = "ENTER OTP",
                    style = MaterialTheme.typography.labelMedium,
                    color = Text3
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sent to +91 $phone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Text3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Dark3)
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = otp,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                otp = it
                                error = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "4 DIGITS",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Dark5
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            cursorColor = Text3
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$attemptsLeft ATTEMPTS REMAINING",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (attemptsLeft <= 1) Error else Text3
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (otp.length != 4) {
                            error = "OTP MUST BE 4 DIGITS"
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
                                    onAuthSuccess(resp.body()!!.token)
                                } else {
                                    error = "WRONG OTP"
                                    attemptsLeft--
                                    if (attemptsLeft <= 0) {
                                        otpSent = false
                                        otp = ""
                                        authId = null
                                        error = "NO ATTEMPTS LEFT"
                                    }
                                }
                            } catch (e: Exception) {
                                error = "ERR: ${e.message ?: e.javaClass.simpleName}"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "VERIFY",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = {
                        otpSent = false
                        otp = ""
                        authId = null
                        error = null
                    }
                ) {
                    Text(
                        text = "WRONG NUMBER?",
                        style = MaterialTheme.typography.labelMedium,
                        color = Text3
                    )
                }
            }

            // Error
            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Error.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = it,
                        color = Error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
