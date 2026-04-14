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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.theme.LocalThemeManager
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: (token: String, userId: String, phone: String) -> Unit
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

    // Verify OTP function
    fun verifyOtp(code: String) {
        if (code.length >= 4 && authId != null && !isLoading) {
            isLoading = true
            scope.launch {
                try {
                    val resp = ApiClient.instance.verifyAuth(authId!!, VerifyRequest(code))
                    if (resp.isSuccessful && resp.body() != null) {
                        val body = resp.body()!!
                        onAuthSuccess(body.token, body.userId, phone)
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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Logo with gradient background
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(theme.actionSubtle, theme.bg2)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "M",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                color = theme.t1
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MAKCO",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = theme.t1
        )
        Text(
            text = "CHENNAI METRO",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = theme.t3
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Title with clear hierarchy
        Text(
            text = if (otpSent) "VERIFY OTP" else "LOGIN",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = theme.t1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (otpSent) "Code sent to +91 ${phone.takeLast(4)}" else "Enter your phone number",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = theme.t2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!otpSent) {
            // Phone input - styled like profile card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(3.dp, theme.outline, RoundedCornerShape(16.dp))
                    .background(theme.bg2)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+91", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t1)
                    Spacer(modifier = Modifier.width(16.dp))
                    BasicTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.titleLarge.copy(color = theme.t1, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        cursorBrush = SolidColor(theme.t1),
                        decorationBox = { innerTextField ->
                            Box {
                                if (phone.isEmpty()) Text("Phone number", style = MaterialTheme.typography.titleLarge, color = theme.t4)
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button with action color
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
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.action, contentColor = theme.bg),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = theme.bg, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else {
                    Text("CONTINUE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        } else {
            // OTP input - single field with auto-submit
            BasicTextField(
                value = otp,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(4)
                    otp = digits
                    // Auto-verify when 4 digits
                    if (digits.length == 4 && authId != null && !isLoading) {
                        verifyOtp(digits)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(3.dp, theme.outline, RoundedCornerShape(12.dp))
                    .background(theme.bg2)
                    .padding(horizontal = 16.dp),
                textStyle = MaterialTheme.typography.headlineLarge.copy(
                    color = theme.t1, 
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                cursorBrush = SolidColor(theme.t1),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (otp.isEmpty()) {
                            Text("0000", style = MaterialTheme.typography.headlineLarge, color = theme.t4)
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Enter 4-digit OTP", style = MaterialTheme.typography.labelLarge, color = theme.t3)

            Spacer(modifier = Modifier.height(12.dp))
            Text("$attemptsLeft attempts remaining", style = MaterialTheme.typography.labelLarge, color = theme.t4)

            Spacer(modifier = Modifier.height(24.dp))

            // Verify button with action color
            Button(
                onClick = {
                    if (otp.length >= 4 && authId != null) {
                        isLoading = true
                        scope.launch {
                            try {
                                val resp = ApiClient.instance.verifyAuth(authId!!, VerifyRequest(otp))
                                if (resp.isSuccessful && resp.body() != null) {
                                    val body = resp.body()!!
                                    onAuthSuccess(body.token, body.userId, phone)
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
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.action, contentColor = theme.bg),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = theme.bg, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else {
                    Text("VERIFY", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { otpSent = false; otp = ""; authId = null; error = null }) {
                Text("Change number", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = theme.action)
            }
        }

        // Error message - styled card
        error?.let {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.error.copy(alpha = 0.15f))
                    .border(2.dp, theme.error, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(it, color = theme.error, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Text(
            "By continuing, you agree to our Terms",
            style = MaterialTheme.typography.labelMedium,
            color = theme.t4
        )
    }
}