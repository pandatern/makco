package com.pandatern.makco.payment

import android.app.Activity
import android.content.Context
import android.util.Log
import `in`.juspay.hyperconstants.HyperConstants
import `in`.juspay.hypersdk.core.HyperFragment
import `in`.juspay.hypersdk.core.JuspayCallback
import `in`.juspay.hypersdk.data.JuspayResponseHandler
import `in`.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter
import org.json.JSONObject

class PaymentManager(
    private val activity: Activity,
    private val callback: PaymentCallback
) {
    companion object {
        private const val TAG = "PaymentManager"
        // Sandbox environment for debug
        private const val MERCHANT_ID = "nammayatriBAP"
        private const val CLIENT_ID = "nammayatriBAP"
        private const val SERVICE = "in.juspay.hyperpay"
    }

    private var hyperFragment: HyperFragment? = null
    private var isInitialized = false

    interface PaymentCallback {
        fun onPaymentSuccess(orderId: String)
        fun onPaymentFailure(orderId: String, status: String, errorMessage: String)
        fun onPaymentCancelled()
        fun onPaymentInProgress(status: String)
    }

    fun initialize() {
        try {
            hyperFragment = HyperFragment()
            hyperFragment?.setCallback(object : HyperPaymentsCallbackAdapter() {
                override fun onEvent(data: JSONObject, response: JuspayResponseHandler?) {
                    handleHyperEvent(data)
                }

                override fun onStartProcessing(data: JSONObject) {
                    Log.d(TAG, "Processing started")
                }
            })

            // Initialize with sandbox config
            val initPayload = JSONObject().apply {
                put("action", "initiate")
                put("service", SERVICE)
                put("clientId", CLIENT_ID)
                put("merchantId", MERCHANT_ID)
                put("environment", "sandbox") // Use sandbox for debug
            }

            hyperFragment?.process(initPayload)
            isInitialized = true
            Log.d(TAG, "HyperSDK initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize HyperSDK", e)
        }
    }

    fun processPayment(sdkPayloadJson: String) {
        if (!isInitialized || hyperFragment == null) {
            Log.e(TAG, "HyperSDK not initialized")
            callback.onPaymentFailure("", "INIT_ERROR", "Payment SDK not ready")
            return
        }

        try {
            val payload = JSONObject(sdkPayloadJson)
            Log.d(TAG, "Processing payment payload")
            hyperFragment?.process(payload)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process payment", e)
            callback.onPaymentFailure("", "PAYLOAD_ERROR", e.message ?: "Failed to process payment")
        }
    }

    private fun handleHyperEvent(data: JSONObject) {
        try {
            val event = data.optString("event", "")
            Log.d(TAG, "HyperEvent: $event")

            when (event) {
                "initiate_result" -> {
                    val payload = data.optJSONObject("payload")
                    val status = payload?.optString("status", "") ?: ""
                    Log.d(TAG, "Init result: $status")
                }

                "process_result" -> {
                    val payload = data.optJSONObject("payload")
                    val status = payload?.optString("status", "") ?: ""
                    val orderId = payload?.optString("order_id", "") ?: ""

                    Log.d(TAG, "Process result: $status, orderId: $orderId")

                    when (status) {
                        "CHARGED" -> {
                            callback.onPaymentSuccess(orderId)
                        }
                        "AUTHENTICATION_FAILED",
                        "AUTHORIZATION_FAILED",
                        "JUSPAY_DECLINED" -> {
                            val error = payload?.optString("error_message", "Payment failed") ?: "Payment failed"
                            callback.onPaymentFailure(orderId, status, error)
                        }
                        "PENDING_VBV",
                        "AUTHORIZING",
                        "STARTED",
                        "NEW" -> {
                            callback.onPaymentInProgress(status)
                        }
                        "CANCELLED" -> {
                            callback.onPaymentCancelled()
                        }
                        else -> {
                            callback.onPaymentInProgress(status)
                        }
                    }
                }

                "back_pressed" -> {
                    callback.onPaymentCancelled()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling HyperEvent", e)
        }
    }

    fun onDestroy() {
        hyperFragment?.onDestroy()
        hyperFragment = null
        isInitialized = false
    }
}
