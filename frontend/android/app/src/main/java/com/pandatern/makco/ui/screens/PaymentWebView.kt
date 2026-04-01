package com.pandatern.makco.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pandatern.makco.ui.theme.*

enum class PaymentState {
    LOADING, LOADED, SUCCESS, FAILED, CANCELLED, TIMEOUT, NETWORK_ERROR
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebView(
    paymentUrl: String,
    onPaymentComplete: () -> Unit,
    onBack: () -> Unit
) {
    var state by remember { mutableStateOf(PaymentState.LOADING) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← BACK", style = MaterialTheme.typography.labelLarge, color = theme.t1)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "PAYMENT",
                style = MaterialTheme.typography.labelMedium,
                color = theme.t3
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(60.dp))
        }

        // Loading
        if (state == PaymentState.LOADING) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MetroBlue,
                trackColor = theme.bg3
            )
        }

        // WebView
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(false)
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                state = PaymentState.LOADING
                                url?.let { handleUrl(it, onPaymentComplete, onBack, { s -> state = s }, { e -> errorMsg = e }) }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                if (state == PaymentState.LOADING) state = PaymentState.LOADED
                                url?.let { handleUrl(it, onPaymentComplete, onBack, { s -> state = s }, { e -> errorMsg = e }) }
                            }

                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                if (request?.isForMainFrame == true) {
                                    state = PaymentState.NETWORK_ERROR
                                    errorMsg = error?.description?.toString() ?: "Connection failed"
                                }
                            }

                            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                                if (request?.isForMainFrame == true && (errorResponse?.statusCode ?: 0) >= 400) {
                                    state = PaymentState.FAILED
                                    errorMsg = "Server error: ${errorResponse?.statusCode}"
                                }
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val url = request?.url?.toString() ?: return false

                                // Handle UPI deep links
                                if (url.startsWith("upi://") || url.startsWith("tez://") || url.startsWith("phonepe://") || url.startsWith("paytmmp://")) {
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, request.url)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        errorMsg = "No UPI app found"
                                        state = PaymentState.FAILED
                                    }
                                    return true
                                }

                                // Handle return URLs
                                handleUrl(url, onPaymentComplete, onBack, { s -> state = s }, { e -> errorMsg = e })
                                return false
                            }
                        }

                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        webViewRef = this
                        loadUrl(paymentUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // State overlays
            when (state) {
                PaymentState.SUCCESS -> {
                    StatusOverlay(
                        title = "PAYMENT SUCCESS",
                        message = "Your ticket is being generated...",
                        color = Success,
                        theme = theme
                    )
                }
                PaymentState.FAILED -> {
                    StatusOverlay(
                        title = "PAYMENT FAILED",
                        message = errorMsg ?: "Payment could not be completed",
                        color = Error,
                        theme = theme,
                        onRetry = {
                            state = PaymentState.LOADING
                            webViewRef?.reload()
                        },
                        onBack = onBack
                    )
                }
                PaymentState.CANCELLED -> {
                    StatusOverlay(
                        title = "PAYMENT CANCELLED",
                        message = "You cancelled the payment",
                        color = MetroGold,
                        theme = theme,
                        onRetry = {
                            state = PaymentState.LOADING
                            webViewRef?.reload()
                        },
                        onBack = onBack
                    )
                }
                PaymentState.TIMEOUT -> {
                    StatusOverlay(
                        title = "TIMEOUT",
                        message = "Payment took too long. Check your bank statement before retrying.",
                        color = MetroGold,
                        theme = theme,
                        onRetry = {
                            state = PaymentState.LOADING
                            webViewRef?.reload()
                        },
                        onBack = onBack
                    )
                }
                PaymentState.NETWORK_ERROR -> {
                    StatusOverlay(
                        title = "NETWORK ERROR",
                        message = errorMsg ?: "Check your internet connection",
                        color = Error,
                        theme = theme,
                        onRetry = {
                            state = PaymentState.LOADING
                            webViewRef?.reload()
                        },
                        onBack = onBack
                    )
                }
                else -> {}
            }
        }
    }
}

private fun handleUrl(
    url: String,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    setState: (PaymentState) -> Unit,
    setError: (String) -> Unit
) {
    when {
        // Success callbacks
        url.contains("ticketBookingStatus") ||
        url.contains("payment/success") ||
        url.contains("status=CHARGED") ||
        url.contains("status=CONFIRMED") -> {
            setState(PaymentState.SUCCESS)
            onSuccess()
        }

        // Failure callbacks
        url.contains("status=AUTHENTICATION_FAILED") -> {
            setState(PaymentState.FAILED)
            setError("Authentication failed. Check your card details.")
        }
        url.contains("status=AUTHORIZATION_FAILED") -> {
            setState(PaymentState.FAILED)
            setError("Bank declined the payment.")
        }
        url.contains("status=JUSPAY_DECLINED") -> {
            setState(PaymentState.FAILED)
            setError("Payment declined by payment gateway.")
        }
        url.contains("payment/failed") ||
        url.contains("status=FAILED") -> {
            setState(PaymentState.FAILED)
            setError("Payment failed.")
        }

        // Cancel callbacks
        url.contains("payment/cancel") ||
        url.contains("status=CANCELLED") -> {
            setState(PaymentState.CANCELLED)
        }

        // Timeout
        url.contains("status=CLIENT_AUTH_TOKEN_EXPIRED") -> {
            setState(PaymentState.TIMEOUT)
        }

        // Pending states
        url.contains("status=PENDING_VBV") ||
        url.contains("status=AUTHORIZING") -> {
            setState(PaymentState.LOADING)
        }
    }
}

@Composable
fun StatusOverlay(
    title: String,
    message: String,
    color: androidx.compose.ui.graphics.Color,
    theme: ThemeManager,
    onRetry: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = theme.t3
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (onRetry != null) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (theme.isDark) Color.White else Color.Black,
                        contentColor = if (theme.isDark) Color.Black else Color.White
                    )
                ) {
                    Text(
                        text = "RETRY",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (onBack != null) {
                TextButton(onClick = onBack) {
                    Text("GO BACK", color = theme.t3)
                }
            }
        }
    }
}
