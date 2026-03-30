# Data Models

## Station

```json
{
  "address": null,
  "code": "SVA|0225",
  "color": null,
  "distance": null,
  "integratedBppConfigId": "ac16fde8-9862-d34b-bbe7-5cea9571bce1",
  "lat": 13.050825,
  "lon": 80.212242,
  "name": "Vadapalani",
  "parentStopCode": null,
  "routeCodes": null,
  "sequenceNum": null,
  "stationType": null,
  "timeTakenToTravelUpcomingStop": null,
  "towards": null
}
```

| Field | Type | Description |
|-------|------|-------------|
| code | string | Unique station identifier (format: "XXX\|XXXX") |
| name | string | Display name |
| lat | float | Latitude |
| lon | float | Longitude |
| integratedBppConfigId | string | BPP integration identifier |
| stationType | string | "START" or "END" in quote context |

## Route

```json
{
  "code": "abd978d5-efc5-4a14-99cb-7df9623d2f56_BLUE_UP",
  "endPoint": {"lat": 13.044682, "lon": 80.248052},
  "integratedBppConfigId": "ac16fde8-9862-d34b-bbe7-5cea9571bce1",
  "longName": "Wimco Nagar Metro - AG-DMS",
  "shortName": "BLUE_UP",
  "startPoint": {"lat": 13.18304, "lon": 80.309036},
  "stops": null,
  "totalStops": null
}
```

| Field | Type | Description |
|-------|------|-------------|
| code | string | Route identifier (includes direction) |
| longName | string | Full route name |
| shortName | string | Route code (BLUE_UP, BLUE_DOWN, GREEN_UP, GREEN_DOWN) |
| startPoint | object | Starting coordinates |
| endPoint | object | Ending coordinates |
| integratedBppConfigId | string | BPP integration identifier |

## Quote

```json
{
  "_type": "SingleJourney",
  "categories": [...],
  "discountedTickets": null,
  "eventDiscountAmount": null,
  "integratedBppConfigId": "ac16fde8-9862-d34b-bbe7-5cea9571bce1",
  "price": 32,
  "priceWithCurrency": {"amount": 32, "currency": "INR"},
  "quantity": 1,
  "quoteId": "36629e7e-64eb-47d0-9536-1c70c8757ad8",
  "routeStations": [],
  "stations": [...],
  "validTill": "2026-03-30T08:50:28.902Z",
  "vehicleType": "METRO"
}
```

| Field | Type | Description |
|-------|------|-------------|
| _type | enum | "SingleJourney" or "ReturnJourney" |
| quoteId | string | Use this to confirm booking |
| price | float | Total fare amount |
| quantity | int | Number of tickets |
| validTill | datetime | Quote expiration time |
| categories | array | Ticket categories with pricing |

## Booking

```json
{
  "_type": "SingleJourney",
  "bookingId": "f1f4adda-00de-4e29-adb3-992658039cc6",
  "city": "std:044",
  "createdAt": "2026-03-30T09:05:49.562Z",
  "discountedTickets": null,
  "eventDiscountAmount": null,
  "googleWalletJWTUrl": null,
  "integratedBppConfigId": "ac16fde8-9862-d34b-bbe7-5cea9571bce1",
  "isFareChanged": false,
  "payment": null,
  "price": 32,
  "priceWithCurrency": {"amount": 32, "currency": "INR"},
  "quantity": 1,
  "quoteCategories": [...],
  "routeStations": [],
  "stations": [...],
  "status": "NEW",
  "tickets": [],
  "updatedAt": "2026-03-30T09:05:49.580Z",
  "validTill": "2026-03-30T09:35:49.569Z",
  "vehicleType": "METRO"
}
```

| Field | Type | Description |
|-------|------|-------------|
| bookingId | string | Unique booking identifier |
| status | enum | NEW, PAYMENT_PENDING, CONFIRMED, CANCELLED |
| price | float | Booking amount |
| payment | object | Payment order (null until payment initiated) |
| tickets | array | Generated tickets (empty until payment confirmed) |
| validTill | datetime | Booking expiration time |

## User Profile

```json
{
  "aadhaarVerified": false,
  "androidId": null,
  "blockedUntil": null,
  "cancellationRate": null,
  "customerReferralCode": "CLGSrX",
  "customerTags": {},
  "firstName": null,
  "gender": "UNKNOWN",
  "hasTakenRide": false,
  "hasTakenValidBusRide": false,
  "id": "fd86b184-27ac-4f89-a7fa-c274692f9602",
  "isBlocked": false,
  "isMultimodalRider": false,
  "maskedMobileNumber": "949...877",
  "publicTransportVersion": "b55c6cdb..."
}
```

| Field | Type | Description |
|-------|------|-------------|
| id | string | User identifier |
| maskedMobileNumber | string | Partially hidden phone number |
| hasTakenRide | boolean | Whether user has completed any ride |
| isBlocked | boolean | Whether user is blocked |
| customerReferralCode | string | User's referral code |

## Payment Order

```json
{
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
        "currency": "INR",
        "merchantId": "nammayatriBAP",
        "orderId": "N7XcmxdqGv",
        "returnUrl": "https://app.moving.tech/wv/ticketBookingStatus"
      }
    }
  },
  "status": "NEW"
}
```
