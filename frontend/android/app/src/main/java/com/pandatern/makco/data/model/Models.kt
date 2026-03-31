package com.pandatern.makco.data.model

import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("stationType") val stationType: String? = null
)

data class Route(
    @SerializedName("code") val code: String,
    @SerializedName("longName") val longName: String,
    @SerializedName("shortName") val shortName: String,
    @SerializedName("startPoint") val startPoint: LatLng?,
    @SerializedName("endPoint") val endPoint: LatLng?
)

data class LatLng(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

data class SearchResponse(
    @SerializedName("quotes") val quotes: List<Any>,
    @SerializedName("searchId") val searchId: String
)

data class Quote(
    @SerializedName("_type") val type: String,
    @SerializedName("quoteId") val quoteId: String,
    @SerializedName("price") val price: Double,
    @SerializedName("priceWithCurrency") val priceWithCurrency: PriceCurrency,
    @SerializedName("stations") val stations: List<Station>,
    @SerializedName("validTill") val validTill: String,
    @SerializedName("vehicleType") val vehicleType: String,
    @SerializedName("categories") val categories: List<QuoteCategory>? = null
)

data class QuoteCategory(
    @SerializedName("categoryName") val categoryName: String? = null,
    @SerializedName("categoryMeta") val categoryMeta: CategoryMeta? = null,
    @SerializedName("categoryPrice") val categoryPrice: PriceCurrency? = null,
    @SerializedName("categoryOfferedPrice") val categoryOfferedPrice: PriceCurrency? = null,
    @SerializedName("categorySelectedQuantity") val categorySelectedQuantity: Int? = null
)

data class CategoryMeta(
    @SerializedName("code") val code: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("tnc") val tnc: String? = null
)

data class PriceCurrency(
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String
)

data class BookingResponse(
    @SerializedName("bookingId") val bookingId: String,
    @SerializedName("status") val status: String,
    @SerializedName("price") val price: Double,
    @SerializedName("priceWithCurrency") val priceWithCurrency: PriceCurrency,
    @SerializedName("stations") val stations: List<Station>,
    @SerializedName("validTill") val validTill: String
)

data class BookingStatus(
    @SerializedName("bookingId") val bookingId: String,
    @SerializedName("status") val status: String,
    @SerializedName("price") val price: Double,
    @SerializedName("payment") val payment: PaymentOrder?,
    @SerializedName("tickets") val tickets: List<Any>
)

data class PaymentOrder(
    @SerializedName("paymentOrder") val order: JuspayOrder,
    @SerializedName("status") val status: String
)

data class JuspayOrder(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("payment_links") val paymentLinks: PaymentLinks,
    @SerializedName("sdk_payload") val sdkPayload: SdkPayload?
)

data class PaymentLinks(
    @SerializedName("web") val web: String?
)

data class SdkPayload(
    @SerializedName("payload") val payload: Map<String, Any>?
)

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("maskedMobileNumber") val maskedMobileNumber: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("hasTakenRide") val hasTakenRide: Boolean
)

data class AuthRequest(
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("mobileCountryCode") val mobileCountryCode: String = "+91"
)

data class AuthResponse(
    @SerializedName("authId") val authId: String,
    @SerializedName("attempts") val attempts: Int,
    @SerializedName("authType") val authType: String
)

data class VerifyRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("deviceToken") val deviceToken: String = "makco_android"
)

data class VerifyResponse(
    @SerializedName("token") val token: String,
    @SerializedName("userId") val userId: String
)

data class SearchRequest(
    @SerializedName("fromStationCode") val fromStationCode: String,
    @SerializedName("toStationCode") val toStationCode: String,
    @SerializedName("quantity") val quantity: Int = 1
)

data class ConfirmRequest(
    @SerializedName("quantity") val quantity: Int = 1
)

data class ErrorResponse(
    @SerializedName("error") val error: String?
)
