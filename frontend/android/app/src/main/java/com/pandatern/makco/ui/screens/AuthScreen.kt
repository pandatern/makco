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
            .background(Black)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black
            )
        )

        Text(
            text = "CHENNAI METRO",
            style = MaterialTheme.typography.labelMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(80.dp))

        if (!otpSent) {
            Text(
                text = "PHONE NUMBER",
                style = MaterialTheme.typography.labelMedium,
                color = Gray500,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                        phone = it
                        error = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("9876543210", color = Gray400) },
                prefix = { Text("+91 ", color = Gray600) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = Gray400,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = White
                ),
                shape = RoundedCornerShape(4.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                                val body = resp.body()!!
                                authId = body.authId
                                attemptsLeft = body.attempts
                                otpSent = true
                            } else {
                                error = "AUTH FAILED"
                            }
                        } catch (e: Exception) {
                            error = "NETWORK ERROR"
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
                shape = RoundedCornerShape(4.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SEND OTP",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        } else {
            Text(
                text = "ENTER OTP",
                style = MaterialTheme.typography.labelMedium,
                color = Gray500,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "+91 $phone  •  $attemptsLeft ATTEMPTS LEFT",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                        otp = it
                        error = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("4 DIGITS", color = Gray400) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = Gray400,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = White
                ),
                shape = RoundedCornerShape(4.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                                val body = resp.body()!!
                                onAuthSuccess(body.token)
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
                            error = "NETWORK ERROR"
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
                shape = RoundedCornerShape(4.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    otpSent = false
                    otp = ""
                    authId = null
                    error = null
                }
            ) {
                Text("CHANGE NUMBER", color = Gray500)
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = Error,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
