# API Reference - Makco Backend API

Complete documentation of all confirmed working API endpoints.

## Base URL

```
https://api.pandatern.tech
```

Or locally:

```
http://localhost:8080
```

## Authentication

All transport endpoints require a valid token in the header:

```
token: <your-session-token>
```

The token is obtained through the phone OTP authentication flow.

## Endpoints

### 1. Authentication

#### Initiate Auth (Send OTP)

```
POST /auth
```

**Request Body:**
```json
{
  "mobileNumber": "9876543210",
  "mobileCountryCode": "+91",
  "merchantId": "NAMMA_YATRI",
  "clientId": "NAMMA_YATRI"
}
```

**Response (200):**
```json
{
  "attempts": 3,
  "authId": "fd52280a-62ad-442c-9358-51c2afae3a5f",
  "authType": "OTP",
  "depotCode": null,
  "isDepotAdmin": null,
  "isPersonBlocked": false,
  "person": null,
  "token": null
}
```

**Key Details:**
- OTP is 4 digits (not 6)
- 3 attempts per auth session
- authId is required for verification

---

#### Verify OTP

```
POST /auth/{authId}/verify
```

**Request Body:**
```json
{
  "otp": "1234",
  "deviceToken": "your_device_token"
}
```

**Response (200):**
```json
{
  "token": "29b7452c-5bb4-463e-97da-44c6eda3b37e",
  "userId": "fd86b184-27ac-4f89-a7fa-c274692f9602"
}
```

**Error Responses:**
- `400 INVALID_AUTH_DATA` - Wrong OTP
- `400 REQUEST_VALIDATION_FAILURE` - Invalid request format (OTP must be 4 digits)

---

### 2. Metro Routes

#### Get Routes

```
GET /frfs/routes?city=chennai&vehicleType="METRO"
```

**Headers:**
```
token: <session-token>
```

**Response (200):**
```json
[
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
]
```

**Available Routes:**
- `BLUE_UP` - Wimco Nagar Metro to AG-DMS
- `BLUE_DOWN` - AG-DMS to Wimco Nagar Metro
- `GREEN_UP` - Vadapalani to Anna Nagar East
- `GREEN_DOWN` - Anna Nagar East to Vadapalani

**IMPORTANT:** vehicleType must be JSON-encoded in query string:
- Correct: `vehicleType=%22METRO%22`
- Wrong: `vehicleType=METRO`

---

### 3. Metro Stations

#### Get Stations

```
GET /frfs/stations?city=chennai&vehicleType="METRO"
```

**Headers:**
```
token: <session-token>
```

**Response (200):** Array of 41 stations
```json
[
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
]
```

**Blue Line Stations (North to South):**
| Code | Name | Lat | Lon |
|------|------|-----|-----|
| SWN\|01EF | Wimco Nagar Metro | 13.18304 | 80.309036 |
| SWD\|01ED | Wimco Nagar Depot | 13.184299 | 80.309093 |
| STV\|01F1 | Thiruvotriyur Metro | 13.172 | 80.305 |
| STT\|01F3 | Thiruvotriyur Theradi Metro | 13.159773 | 80.302449 |
| STG\|01F7 | Tollgate Metro | 13.143 | 80.296 |
| STR\|01FB | Tondiarpet Metro | 13.124 | 80.289 |
| STC\|01FD | Thiagaraya College Metro | 13.116 | 80.284 |
| SWA\|0101 | Washermanpet | 13.107064 | 80.280528 |
| SNW\|01F9 | New Washermenpet Metro | 13.107064 | 80.280528 |
| SMA\|0103 | Mannadi | 13.095177 | 80.286164 |
| SHC\|0105 | High Court | 13.087369 | 80.285021 |
| SCC\|0201 | Puratchi Thalaivar Dr. M.G. Ramachandran Central | 13.081426 | 80.272887 |
| SGE\|0109 | Government Estate | 13.069557 | 80.272842 |
| SLI\|0111 | LIC | 13.064511 | 80.266065 |
| STL\|0113 | Thousand Lights | 13.058198 | 80.258056 |
| SGM\|0115 | AG-DMS | 13.044682 | 80.248052 |
| STE\|0117 | Teynampet | 13.037904 | 80.247029 |
| SCR\|0119 | Nandanam | 13.03139 | 80.239969 |
| SSA\|0121 | Saidapet | 13.023717 | 80.228208 |
| SLM\|0123 | Little Mount | 13.014712 | 80.223993 |
| SGU\|0125 | Guindy | 13.00924 | 80.213199 |
| SAL\|0231 | Arignar Anna Alandur | 13.004713 | 80.20145 |
| SOT\|0129 | OTA - Nanganallur Road | 12.999933 | 80.193985 |
| SME\|0131 | Meenambakkam | 12.987656 | 80.176505 |
| SAP\|0133 | Chennai International Airport | 12.980826 | 80.1642 |

**Green Line Stations:**
| Code | Name | Lat | Lon |
|------|------|-----|-----|
| SMM\|0233 | St. Thomas Mount | 12.995128 | 80.19864 |
| SSI\|0229 | Ekkattuthangal | 13.017044 | 80.20594 |
| SAN\|0227 | Ashok Nagar | 13.035534 | 80.21114 |
| SVA\|0225 | Vadapalani | 13.050825 | 80.212242 |
| SAR\|0223 | Arumbakkam | 13.062058 | 80.211581 |
| SCM\|0221 | Puratchi Thalaivi Dr. J. Jayalalithaa CMBT | 13.068568 | 80.203882 |
| SKO\|0219 | Koyambedu | 13.073708 | 80.194869 |
| STI\|0217 | Thirumangalam | 13.085259 | 80.201575 |
| SAE\|0213 | Anna Nagar East | 13.084794 | 80.21866 |
| SAT\|0215 | Anna Nagar Tower | 13.084975 | 80.208727 |
| SSN\|0211 | Shenoy Nagar | 13.078697 | 80.225133 |
| SPC\|0209 | Pachaiyappas College | 13.07557 | 80.232347 |
| SKM\|0207 | Kilpauk | 13.077508 | 80.242867 |
| SNP\|0205 | Nehru Park | 13.078625 | 80.250855 |
| SEG\|0203 | Egmore | 13.079059 | 80.261098 |

---

### 4. Search (Get Fare)

#### Initiate Search

```
POST /frfs/search?city=chennai&vehicleType="METRO"
```

**Headers:**
```
token: <session-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "fromStationCode": "SVA|0225",
  "toStationCode": "SGM|0115",
  "quantity": 1
}
```

**Response (200):**
```json
{
  "quotes": [],
  "searchId": "aec9a861-f3e2-4f45-beed-2a3764902fa7"
}
```

**Key Details:**
- Quotes are generated asynchronously
- Poll the quote endpoint after 2-3 seconds
- searchId is valid for a limited time

---

#### Get Quotes (Fare)

```
GET /frfs/search/{searchId}/quote?city=chennai&vehicleType="METRO"
```

**Headers:**
```
token: <session-token>
```

**Response (200):**
```json
[
  {
    "_type": "SingleJourney",
    "categories": [
      {
        "categoryFinalPrice": null,
        "categoryId": "39f68d39-6365-47e9-b59b-61769eae2583",
        "categoryMeta": {
          "categoryOrder": null,
          "code": "ADULT",
          "description": "Standard ticket category for adult passengers",
          "title": "ADULT",
          "tnc": "Standard terms and conditions apply"
        },
        "categoryName": "ADULT",
        "categoryOfferedPrice": {"amount": 32, "currency": "INR"},
        "categoryPrice": {"amount": 32, "currency": "INR"},
        "categorySelectedQuantity": 1
      }
    ],
    "price": 32,
    "priceWithCurrency": {"amount": 32, "currency": "INR"},
    "quantity": 1,
    "quoteId": "36629e7e-64eb-47d0-9536-1c70c8757ad8",
    "stations": [
      {"name": "Vadapalani", "stationType": "START", "sequenceNum": 1},
      {"name": "AG-DMS", "stationType": "END", "sequenceNum": 2}
    ],
    "validTill": "2026-03-30T08:50:28.902Z",
    "vehicleType": "METRO"
  },
  {
    "_type": "ReturnJourney",
    "price": 64,
    "quoteId": "c2230738-1576-4d6f-9007-01c6e466a1cc"
  }
]
```

**Quote Types:**
- `SingleJourney` - One-way ticket
- `ReturnJourney` - Round-trip ticket (double price)

---

### 5. Booking

#### Confirm Booking

```
POST /frfs/quote/{quoteId}/confirm?city=chennai&vehicleType="METRO"
```

**Headers:**
```
token: <session-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "quantity": 1
}
```

**Response (200):**
```json
{
  "_type": "SingleJourney",
  "bookingId": "f1f4adda-00de-4e29-adb3-992658039cc6",
  "city": "std:044",
  "createdAt": "2026-03-30T09:05:49.562054297Z",
  "integratedBppConfigId": "ac16fde8-9862-d34b-bbe7-5cea9571bce1",
  "isFareChanged": false,
  "payment": null,
  "price": 32,
  "priceWithCurrency": {"amount": 32, "currency": "INR"},
  "quantity": 1,
  "status": "NEW",
  "tickets": [],
  "validTill": "2026-03-30T09:35:49.569573716Z",
  "vehicleType": "METRO"
}
```

---

#### Get Booking Status (includes payment data)

```
GET /frfs/booking/{bookingId}/status?city=chennai&vehicleType="METRO"
```

**Headers:**
```
token: <session-token>
```

**Response (200):**
```json
{
  "bookingId": "f1f4adda-00de-4e29-adb3-992658039cc6",
  "status": "PAYMENT_PENDING",
  "price": 32,
  "payment": {
    "paymentOrder": {
      "id": "ordeh_f092a3c10ef54bbda967be3660fcee0b",
      "order_id": "N7XcmxdqGv",
      "payment_links": {
        "web": "https://payments.juspay.in/payment-page/order/ordeh_f092a3c10ef54bbda967be3660fcee0b"
      },
      "sdk_payload": {
        "payload": {
          "action": "paymentPage",
          "amount": "32.0",
          "clientAuthToken": "tkn_cadc00a628794a538a915d4f458ea9b7",
          "clientAuthTokenExpiry": "2026-03-30T09:20:49Z",
          "clientId": "nammayatriBAP",
          "currency": "INR",
          "customerId": "fd86b184-27ac-4f89-a7fa-c274692f9602",
          "customerPhone": "9498312877",
          "environment": "production",
          "merchantId": "nammayatriBAP",
          "orderId": "N7XcmxdqGv",
          "returnUrl": "https://app.moving.tech/wv/ticketBookingStatus",
          "service": "in.juspay.hyperpay"
        }
      }
    },
    "status": "NEW"
  },
  "tickets": []
}
```

**Booking Statuses:**
- `NEW` - Booking created
- `PAYMENT_PENDING` - Awaiting payment
- `CONFIRMED` - Payment received
- `CANCELLED` - Cancelled

---

### 6. User Profile

#### Get Profile

```
GET /profile
```

**Headers:**
```
token: <session-token>
```

**Response (200):**
```json
{
  "id": "fd86b184-27ac-4f89-a7fa-c274692f9602",
  "maskedMobileNumber": "949...877",
  "firstName": null,
  "lastName": null,
  "gender": "UNKNOWN",
  "isBlocked": false,
  "hasTakenRide": false,
  "hasTakenValidBusRide": false,
  "customerReferralCode": "CLGSrX",
  "publicTransportVersion": "b55c6cdb..."
}
```

---

### 7. Ticket Bookings List

#### Get All Ticket Bookings

```
GET /ticket/bookings/v2?city=chennai&vehicleType="METRO"
```

**Headers:**
```
token: <session-token>
```

**Response (200):**
```json
[]
```

Returns array of ticket bookings for the authenticated user.

---

## Other Endpoints (Confirmed Working)

### Firebase Remote Config
```
POST https://firebaseremoteconfig.googleapis.com/v1/projects/876430001318/namespaces/firebase:fetch?key=AIzaSyD5iVZHCLDX5qZG-3v0rG5bbhA-Q8BmHkM
```
Returns 170 config entries. No auth required beyond API key.

### OTA Bundle
```
GET https://airborne.juspay.in/release/movingtech/chennaione
```
Returns bundle metadata and download URL. No auth.

### Juspay Config
```
GET https://assets.juspay.in/juspay/payments/2.0/release/v1-config.zip
```
Payment gateway configuration. No auth.

### Log Endpoint
```
POST https://logs.moving.tech/analytics
```
Accepts any JSON payload. No auth.

---

## Error Codes

| Code | Meaning |
|------|---------|
| INVALID_TOKEN | Token is missing or invalid |
| INVALID_AUTH_DATA | Wrong OTP |
| REQUEST_VALIDATION_FAILURE | Invalid request format |
| INTERNAL_ERROR | Server error |

## Rate Limiting

Not observed. Multiple concurrent requests accepted.

## CORS

Not tested from browser context.
