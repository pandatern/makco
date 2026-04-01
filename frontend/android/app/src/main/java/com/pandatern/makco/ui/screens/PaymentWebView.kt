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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pandatern.makco.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebView(
    paymentUrl: String,
    onPaymentComplete: () -> Unit,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← BACK", style = MaterialTheme.typography.labelLarge, color = White)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "PAYMENT",
                style = MaterialTheme.typography.labelMedium,
                color = Gray3
            )

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(60.dp))
        }

        // Loading indicator
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MetroBlue,
                trackColor = Dark3
            )
        }

        // WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(false)

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            errorResponse: WebResourceError?
                        ) {
                            error = errorResponse?.description?.toString() ?: "Payment page error"
                            isLoading = false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false

                            // Detect payment completion
                            if (url.contains("ticketBookingStatus") ||
                                url.contains("payment/success") ||
                                url.contains("status=CONFIRMED")) {
                                onPaymentComplete()
                                return true
                            }

                            // Detect payment failure
                            if (url.contains("payment/failed") ||
                                url.contains("status=FAILED")) {
                                error = "Payment failed"
                                return true
                            }

                            return false
                        }
                    }

                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                    loadUrl(paymentUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Error overlay
        error?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Black.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ERROR",
                        style = MaterialTheme.typography.labelMedium,
                        color = Error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray4
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(onClick = onBack) {
                        Text("GO BACK", color = White)
                    }
                }
            }
        }
    }
}
