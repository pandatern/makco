package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "MAKCO",
                style = MaterialTheme.typography.displayLarge,
                color = White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "CHENNAI METRO",
                style = MaterialTheme.typography.labelMedium,
                color = Gray500
            )

            Spacer(modifier = Modifier.height(72.dp))

            if (!otpSent) {
                // Phone input
                Text(
                    text = "PHONE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+91",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray600,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                phone = it
                                error = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("9876543210", color = Gray300) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = Gray300,
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            cursorColor = Gray500
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

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
                                error = "NETWORK ERROR"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
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
                // OTP input
                Text(
                    text = "ENTER OTP",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "+91 $phone",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                            otp = it
                            error = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("4 DIGITS", color = Gray300) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = White,
                        unfocusedBorderColor = Gray300,
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        cursorColor = Gray500
                    ),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$attemptsLeft ATTEMPTS LEFT",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (attemptsLeft <= 1) Error else Gray400
                )

                Spacer(modifier = Modifier.height(28.dp))

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
                                error = "NETWORK ERROR"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
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
                        text = "CHANGE NUMBER",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gray500
                    )
                }
            }

            // Error
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
}
