package com.pandatern.makco.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandatern.makco.R
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

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Animate entrance
    val startOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        tween(400),
        label = "offset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        // Animated header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .graphicsLayer {
                    translationY = startOffset
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(theme.bg2, theme.bg)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Animated M icon
                    val iconScale by animateFloatAsState(
                        targetValue = if (visible) 1f else 0.8f,
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "icon"
                    )
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                            }
                            .background(theme.t1),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = theme.bg)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("MAKCO", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black), color = theme.t1)
                        Text("Chennai Metro", style = MaterialTheme.typography.labelMedium, color = theme.t3)
                    }
                }
            }
        }

        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .graphicsLayer {
                    translationY = startOffset
                }
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(300, delayMillis = 100))) {
                Column {
                    Text(
                        text = if (otpSent) "Enter OTP" else "Login",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = theme.t1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (otpSent) "We sent a code to +91 ${phone.takeLast(4)}" else "Enter your phone number to continue",
                        style = MaterialTheme.typography.bodyMedium,
                        color = theme.t3
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = visible, enter = fadeIn(tween(300, delayMillis = 200))) {
                if (!otpSent) {
                    // Phone input styled
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .border(2.dp, theme.outline, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(theme.bg2)
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("+91", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = theme.t2)
                            Spacer(modifier = Modifier.width(16.dp))
                            BasicTextField(
                                value = phone,
                                onValueChange = { if (it.length <= 10) phone = it },
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.titleLarge.copy(color = theme.t1),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.t1),
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
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = theme.bg, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Continue", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                } else {
                    // OTP input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                    .height(64.dp)
                                    .border(2.dp, theme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                    .background(theme.bg2)
                                    .padding(8.dp),
                                textStyle = MaterialTheme.typography.headlineSmall.copy(color = theme.t1, fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(theme.t1),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) { innerTextField() }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$attemptsLeft attempts remaining", style = MaterialTheme.typography.labelSmall, color = theme.t4)

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
                                    } catch (e: Exception) { error = "Error" }
                                    isLoading = false
                                }
                            }
                        },
                        enabled = otp.length >= 4 && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.t1, contentColor = theme.bg),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = theme.bg, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Verify", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { otpSent = false; otp = ""; authId = null; error = null }) {
                        Text("Change number", style = MaterialTheme.typography.labelMedium, color = theme.t4)
                    }
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .background(theme.t1.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Text(it, color = theme.t1, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text("By continuing, you agree to our Terms", style = MaterialTheme.typography.labelSmall, color = theme.t4, modifier = Modifier.padding(bottom = 32.dp))
        }
    }
}