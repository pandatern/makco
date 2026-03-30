# Payment Integration

## Overview

The metro booking API uses Juspay as the payment gateway. Juspay is an Indian payment orchestration platform that supports UPI, cards, wallets, and net banking.

## Payment Flow

```
Booking Created -> Payment Order Generated -> User Pays -> Ticket Issued
```

## Juspay Integration

### Payment Order

When you check booking status, the response includes a full Juspay payment order:

```json
{
  "payment": {
    "paymentOrder": {
      "id": "ordeh_f092a3c10ef54bbda967be3660fcee0b",
      "order_id": "N7XcmxdqGv",
      "payment_links": {
        "web": "https://payments.juspay.in/payment-page/order/..."
      },
      "sdk_payload": {
        "payload": {
          "action": "paymentPage",
          "amount": "32.0",
          "clientAuthToken": "tkn_...",
          "clientId": "nammayatriBAP",
          "currency": "INR",
          "merchantId": "nammayatriBAP",
          "orderId": "N7XcmxdqGv",
          "returnUrl": "https://app.moving.tech/wv/ticketBookingStatus",
          "service": "in.juspay.hyperpay"
        }
      }
    }
  }
}
```

### Key Payment Fields

| Field | Description |
|-------|-------------|
| `order_id` | Juspay order identifier |
| `clientAuthToken` | Auth token for payment session |
| `clientAuthTokenExpiry` | Token validity window |
| `amount` | Payment amount in INR |
| `currency` | Always "INR" |
| `merchantId` | Juspay merchant: "nammayatriBAP" |
| `clientId` | Juspay client: "nammayatriBAP" |
| `returnUrl` | Callback URL after payment |
| `service` | "in.juspay.hyperpay" |
| `environment` | "production" |

### Payment Methods Supported

- **UPI** (PhonePe, Google Pay, Paytm, BHIM)
- **UPI Intent** (deep link to UPI apps)
- **UPI QR** (scan to pay)
- **Credit/Debit Cards**
- **Net Banking**
- **Wallets**

## Payment Options

### Option 1: Web Payment Page (Simplest)

Redirect user to the Juspay payment page:

```python
payment_url = status_data["payment"]["paymentOrder"]["payment_links"]["web"]
# "https://payments.juspay.in/payment-page/order/ordeh_..."

# Redirect user
return RedirectResponse(url=payment_url)
```

### Option 2: Juspay SDK (In-App)

Use the SDK payload for native payment experience:

```javascript
// JavaScript (for PWA or React Native)
const sdkPayload = payment_order.sdk_payload.payload;

// Initialize Juspay
const hyperServices = new HyperServices();
hyperServices.initiate(sdkPayload, callback);
```

### Option 3: UPI Intent (Mobile)

Extract UPI payment URL from Juspay response and open UPI app:

```python
# The payment page will handle UPI intent generation
# Or use the web payment link which auto-detects UPI apps
```

## Payment Callback

After payment, Juspay redirects to the return URL:
```
https://app.moving.tech/wv/ticketBookingStatus
```

This page handles:
- Payment success confirmation
- Payment failure display
- Ticket display after successful payment

## Payment Configuration (From OTA Bundle)

```json
{
  "merchant_id": "nammayatriBAP",
  "client_id": "nammayatriBAP",
  "upi_merchant_id": "picasso",
  "environment": "production",
  "api_endpoints": {
    "production": "https://payments.juspay.in",
    "sandbox": "https://payments.sandbox.juspay.in"
  },
  "assets": {
    "production": "https://assets.juspay.in",
    "sandbox": "https://sandbox.assets.juspay.in"
  }
}
```

## Payment Statuses

| Status | Description |
|--------|-------------|
| `NEW` | Payment order created |
| `PENDING` | Payment in progress |
| `CHARGED` | Payment successful |
| `AUTHENTICATION_FAILED` | Payment auth failed |
| `AUTHORIZATION_FAILED` | Payment authorization failed |
| `JUSPAY_DECLINED` | Juspay declined |
| `INTERNAL_ERROR` | System error |

## Refund Flow

Not tested. Expected flow:
1. Cancel booking
2. Refund initiated
3. Refund processed to original payment method
4. Timeline: 5-7 days (bank dependent)

## Testing Payment

For testing without real payment:
1. Use Juspay sandbox environment
2. Switch `environment` from "production" to "sandbox"
3. Use sandbox API endpoints
4. Use test card/UPI details

## Juspay Config Download

```
GET https://assets.juspay.in/juspay/payments/2.0/release/v1-config.zip
```

Returns:
- UPI app configurations
- Gateway routing rules
- Payment method availability
- Supported banks
- Card schemes (Visa, Mastercard, RuPay)

## Security Notes

- clientAuthToken is short-lived (~15 minutes)
- Payment is processed by Juspay, not our backend
- PCI compliance handled by Juspay
- No card data touches our servers
- UPI PIN entry is in the user's UPI app
