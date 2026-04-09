package com.pandatern.makco.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.LocalThemeManager
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo
        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = theme.t1
        )
        Text(
            text = "Chennai Metro",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = theme.t2
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = if (otpSent) "Enter OTP" else "Login",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = theme.t1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (otpSent) "We sent a code to +91 ${phone.takeLast(4)}" else "Enter your phone number",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = theme.t2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!otpSent) {
            // Phone input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, theme.outline, RoundedCornerShape(12.dp))
                    .background(theme.bg2)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+91", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = theme.t2)
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = theme.t1),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        cursorBrush = SolidColor(theme.t1),
                        decorationBox = { innerTextField ->
                            Box {
                                if (phone.isEmpty()) Text("Phone number", style = MaterialTheme.typography.titleMedium, color = theme.t4)
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue button
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
                            } catch (e: Exception) { error = "Network error" }
                            isLoading = false
                        }
                    }
                },
                enabled = phone.length >= 10 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = theme.bg, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("CONTINUE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        } else {
            // OTP input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { i ->
                    BasicTextField(
                        value = if (i < otp.length) otp[i].toString() else "",
                        onValueChange = { char ->
                            if (char.length <= 1 && char.all { it.isDigit() }) {
                                val newOtp = StringBuilder(otp)
                                if (newOtp.length <= i) newOtp.append(" ")
                                if (i < newOtp.length) newOtp[i] = char[0]
                                otp = newOtp.toString().trim()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .border(2.dp, theme.outline, RoundedCornerShape(8.dp))
                            .background(theme.bg2)
                            .padding(8.dp),
                        textStyle = MaterialTheme.typography.titleLarge.copy(color = theme.t1, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        cursorBrush = SolidColor(theme.t1),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.Center) { innerTextField() }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("$attemptsLeft attempts left", style = MaterialTheme.typography.labelSmall, color = theme.t4)

            Spacer(modifier = Modifier.height(16.dp))

            // Verify button
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
                            } catch (e: Exception) { error = "Error" }
                            isLoading = false
                        }
                    }
                },
                enabled = otp.length >= 4 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = theme.bg, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("VERIFY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { otpSent = false; otp = ""; authId = null; error = null }) {
                Text("Change number", style = MaterialTheme.typography.labelMedium, color = theme.t4)
            }
        }

        // Error message
        error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.t1.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Text(it, color = theme.t1, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            "By continuing, you agree to our Terms",
            style = MaterialTheme.typography.labelSmall,
            color = theme.t4
        )
    }
}