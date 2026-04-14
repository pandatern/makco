package com.pandatern.makco.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.LocalThemeManager
import com.pandatern.makco.ui.theme.LightGreen
import com.pandatern.makco.ui.theme.LightRed
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: (token: String, userId: String, phone: String) -> Unit
) {
    val theme = LocalThemeManager.current
    val scope = rememberCoroutineScope()

    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var authId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var otpSent by remember { mutableStateOf(false) }
    var attemptsLeft by remember { mutableStateOf(3) }

    val actionColor = if (theme.isDark) LightGreen else LightRed

    // Verify OTP
    fun verifyOtp(code: String) {
        if (code.length >= 4 && authId != null && !isLoading) {
            isLoading = true
            error = null
            scope.launch {
                try {
                    val resp = ApiClient.instance.verifyAuth(authId!!, VerifyRequest(code))
                    if (resp.isSuccessful && resp.body() != null) {
                        val body = resp.body()!!
                        onAuthSuccess(body.token, body.userId, phone)
                    } else {
                        attemptsLeft--
                        error = "Invalid code. $attemptsLeft attempts left."
                        if (attemptsLeft <= 0) {
                            otpSent = false
                            otp = ""
                            attemptsLeft = 3
                            error = null
                        }
                    }
                } catch (e: Exception) { 
                    error = "Connection failed" 
                }
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Minimal logo
        Text(
            "MAKCO",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp
            ),
            color = theme.t1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Chennai Metro",
            style = MaterialTheme.typography.bodyMedium,
            color = theme.t3
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Phone input
        if (!otpSent) {
            Text(
                "Enter your phone",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Phone field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, theme.outline, RoundedCornerShape(12.dp))
                    .background(theme.bg2)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+91", style = MaterialTheme.typography.titleMedium, color = theme.t2)
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = theme.t1),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        cursorBrush = SolidColor(actionColor),
                        singleLine = true,
                        decorationBox = { inner ->
                            Box {
                                if (phone.isEmpty()) {
                                    Text("Phone number", style = MaterialTheme.typography.titleMedium, color = theme.t4)
                                }
                                inner()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                                } else {
                                    error = "Failed to send code"
                                }
                            } catch (e: Exception) { 
                                error = "Network error" 
                            }
                            isLoading = false
                        }
                    }
                },
                enabled = phone.length >= 10 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = actionColor,
                    disabledContainerColor = theme.bg3
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Continue", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
            }
        } else {
            // OTP verification
            Text(
                "Enter code",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = theme.t1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Sent to +91 ${phone.takeLast(3)}****${phone.takeLast(1)}",
                style = MaterialTheme.typography.bodyMedium,
                color = theme.t3
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OTP field
            BasicTextField(
                value = otp,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(4)
                    otp = digits
                    if (digits.length == 4) verifyOtp(digits)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, if (error != null) theme.err else theme.outline, RoundedCornerShape(12.dp))
                    .background(theme.bg2)
                    .padding(horizontal = 16.dp),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = theme.t1,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                cursorBrush = SolidColor(actionColor),
                singleLine = true,
                decorationBox = { inner ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (otp.isEmpty()) {
                            Text("• • • •", style = MaterialTheme.typography.headlineSmall, color = theme.t4)
                        }
                        inner()
                    }
                }
            )

            // Error message
            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.err
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Verify button
            Button(
                onClick = { verifyOtp(otp) },
                enabled = otp.length >= 4 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = actionColor,
                    disabledContainerColor = theme.bg3
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Verify", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Change number
            TextButton(
                onClick = { 
                    otpSent = false
                    otp = ""
                    authId = null
                    error = null
                }
            ) {
                Text(
                    "Change number",
                    style = MaterialTheme.typography.bodyMedium,
                    color = actionColor
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            "By continuing, you agree to our Terms",
            style = MaterialTheme.typography.bodySmall,
            color = theme.t4,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}
