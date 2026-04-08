package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pandatern.makco.R
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onAuthSuccess: (token: String) -> Unit) {
    val theme = LocalThemeManager.current
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var authId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var otpSent by remember { mutableStateOf(false) }
    var attemptsLeft by remember { mutableStateOf(3) }
    val scope = rememberCoroutineScope()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 28.dp)
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = {-20})
        ) {
            Column {
                // Logo with icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.ic_location), contentDescription = "M", colorFilter = ColorFilter.tint(theme.t1), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("MAKCO", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black), color = theme.t1)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("CHENNAI METRO", style = MaterialTheme.typography.labelMedium, color = theme.t3)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedVisibility(visible = visible, enter = fadeIn(tween(400, delayMillis = 100))) {
            Column {
                Text(
                    text = if (otpSent) "ENTER OTP" else "PHONE NUMBER",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.t4
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (!otpSent) {
                    // Phone input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(0.dp))
                            .border(2.dp, theme.outline)
                            .background(theme.bg2)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("+91", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = theme.t2)
                        Spacer(modifier = Modifier.width(12.dp))
                        BasicTextField(
                            value = phone,
                            onValueChange = { if (it.length <= 10) phone = it },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = theme.t1),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.t1)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (phone.length >= 10) {
                                isLoading = true
                                error = null
                                scope.launch {
                                    try {
                                        val resp = ApiClient.instance.initiateAuth(AuthRequest(phone))
                                        if (resp.isSuccessful && resp.body() != null) {
                                            authId = resp.body()!!.authId
                                            otpSent = true
                                        } else error = "Failed to send OTP"
                                    } catch (e: Exception) { error = "Error: ${e.message}" }
                                    isLoading = false
                                }
                            }
                        },
                        enabled = phone.length >= 10 && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg)
                    ) {
                        AnimatedContent(
                            targetState = isLoading,
                            label = "btn"
                        ) { loading ->
                            Text(if (loading) "SENDING..." else "SEND OTP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                } else {
                    // OTP input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(4) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .border(2.dp, theme.outline)
                                    .background(theme.bg2),
                                contentAlignment = Alignment.Center
                            ) {
                                BasicTextField(
                                    value = if (i < otp.length) otp[i].toString() else "",
                                    onValueChange = { char ->
                                        if (char.length <= 1 && char.all { it.isDigit() }) {
                                            val newOtp = StringBuilder(otp)
                                            if (char.isNotEmpty()) {
                                                if (newOtp.length <= i) newOtp.append(" ")
                                                newOtp[i] = char[0]
                                                otp = newOtp.toString().trim()
                                            }
                                        }
                                    },
                                    textStyle = MaterialTheme.typography.headlineSmall.copy(color = theme.t1, fontWeight = FontWeight.Bold),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.t1),
                                    modifier = Modifier
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$attemptsLeft attempts left", style = MaterialTheme.typography.labelSmall, color = if (attemptsLeft <= 1) theme.t1 else theme.t4)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (otp.length >= 4 && authId != null) {
                                isLoading = true
                                scope.launch {
                                    try {
                                        val resp = ApiClient.instance.verifyAuth(authId!!, VerifyRequest(otp))
                                        if (resp.isSuccessful && resp.body() != null) {
                                            onAuthSuccess(resp.body()!!.token)
                                        } else {
                                            attemptsLeft--
                                            error = "Wrong OTP"
                                            if (attemptsLeft <= 0) {
                                                otpSent = false
                                                otp = ""
                                                attemptsLeft = 3
                                            }
                                        }
                                    } catch (e: Exception) { error = "Error: ${e.message}" }
                                    isLoading = false
                                }
                            }
                        },
                        enabled = otp.length >= 4 && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg)
                    ) {
                        AnimatedContent(targetState = isLoading, label = "btn2") { loading ->
                            Text(if (loading) "VERIFYING..." else "VERIFY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { otpSent = false; otp = ""; authId = null; error = null }) {
                        Text("WRONG NUMBER?", style = MaterialTheme.typography.labelMedium, color = theme.t4)
                    }
                }

                // Error
                error?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().background(theme.t1.copy(alpha = 0.1f)).padding(14.dp)) {
                        Text(it, color = theme.t1, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}