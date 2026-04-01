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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            Text(
                text = "MAKCO",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black
                ),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "CHENNAI METRO",
                style = MaterialTheme.typography.labelMedium,
                color = theme.t4
            )

            Spacer(modifier = Modifier.weight(0.2f))

            if (!otpSent) {
                Text(
                    text = "ENTER YOUR PHONE",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.t3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(theme.bg2)
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+91",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = theme.t3
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
                        placeholder = { Text("9876543210", color = theme.t4) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = theme.t1,
                            unfocusedTextColor = theme.t1,
                            cursorColor = theme.t3
                        ),
                        textStyle = MaterialTheme.typography.titleLarge,
                        singleLine = true,
                        enabled = !isLoading
                    )
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
                                error = "ERR: ${e.message}"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (theme.isDark) Color.White else Color.Black,
                        contentColor = if (theme.isDark) Color.Black else Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = theme.bg, strokeWidth = 2.dp)
                    } else {
                        Text("CONTINUE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            } else {
                Text(
                    text = "ENTER OTP",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.t3
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sent to +91 $phone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.t4
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(theme.bg2)
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
                        placeholder = { Text("4 DIGITS", color = theme.t4) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = theme.t1,
                            unfocusedTextColor = theme.t1,
                            cursorColor = theme.t3
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        singleLine = true,
                        enabled = !isLoading
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$attemptsLeft ATTEMPTS REMAINING",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (attemptsLeft <= 1) Error else theme.t4
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
                                error = "ERR: ${e.message}"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (theme.isDark) Color.White else Color.Black,
                        contentColor = if (theme.isDark) Color.Black else Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = theme.bg, strokeWidth = 2.dp)
                    } else {
                        Text("VERIFY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = { otpSent = false; otp = ""; authId = null; error = null }) {
                    Text("WRONG NUMBER?", style = MaterialTheme.typography.labelMedium, color = theme.t4)
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().background(Error.copy(alpha = 0.1f)).padding(16.dp)
                ) {
                    Text(it, color = Error, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
