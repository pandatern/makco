# Booking Flow

## Complete Pipeline

The booking flow consists of 4 steps: Search -> Quote -> Confirm -> Payment.

## Flow Diagram

```
User selects source and destination stations
        |
        v
[STEP 1: SEARCH]
POST /frfs/search?city=chennai&vehicleType="METRO"
Body: {fromStationCode, toStationCode, quantity}
Response: {searchId}
        |
        v
[STEP 2: QUOTE] (wait 2-3 seconds)
GET /frfs/search/{searchId}/quote?city=chennai&vehicleType="METRO"
Response: [{_type, price, quoteId, categories, stations}]
        |
        v
[STEP 3: CONFIRM BOOKING]
POST /frfs/quote/{quoteId}/confirm?city=chennai&vehicleType="METRO"
Body: {quantity}
Response: {bookingId, status: "NEW", price}
        |
        v
[STEP 4: PAYMENT STATUS]
GET /frfs/booking/{bookingId}/status?city=chennai&vehicleType="METRO"
Response: {status: "PAYMENT_PENDING", payment: {juspay order}}
        |
        v
[STEP 5: PAYMENT]
User completes payment via Juspay
        |
        v
[STEP 6: TICKET]
After payment, ticket is generated with QR code
```

## Implementation

### Step 1: Search

```python
import requests

TOKEN = "your-session-token"
BASE = "https://api.moving.tech/pilot/app/v2"

# Initiate search
response = requests.post(
    f"{BASE}/frfs/search",
    params={
        "city": "chennai",
        "vehicleType": '"METRO"'
    },
    json={
        "fromStationCode": "SVA|0225",  # Vadapalani
        "toStationCode": "SGM|0115",    # AG-DMS
        "quantity": 1
    },
    headers={"token": TOKEN}
)

search_id = response.json()["searchId"]
```

### Step 2: Get Quotes

```python
import time

time.sleep(2)  # Wait for quote generation

response = requests.get(
    f"{BASE}/frfs/search/{search_id}/quote",
    params={
        "city": "chennai",
        "vehicleType": '"METRO"'
    },
    headers={"token": TOKEN}
)

quotes = response.json()

# quotes[0] = SingleJourney
single = quotes[0]
quote_id = single["quoteId"]
price = single["price"]  # 32 INR

# quotes[1] = ReturnJourney (if available)
if len(quotes) > 1:
    return_quote = quotes[1]
    return_price = return_quote["price"]  # 64 INR
```

### Step 3: Confirm Booking

```python
response = requests.post(
    f"{BASE}/frfs/quote/{quote_id}/confirm",
    params={
        "city": "chennai",
        "vehicleType": '"METRO"'
    },
    json={"quantity": 1},
    headers={"token": TOKEN}
)

booking = response.json()
booking_id = booking["bookingId"]
status = booking["status"]  # "NEW"
```

### Step 4: Get Payment Details

```python
response = requests.get(
    f"{BASE}/frfs/booking/{booking_id}/status",
    params={
        "city": "chennai",
        "vehicleType": '"METRO"'
    },
    headers={"token": TOKEN}
)

status_data = response.json()
payment_status = status_data["status"]  # "PAYMENT_PENDING"

# Juspay payment order
payment = status_data["payment"]["paymentOrder"]
order_id = payment["order_id"]  # "N7XcmxdqGv"

# Payment links
payment_links = payment["payment_links"]
web_url = payment_links["web"]  # Juspay payment page

# SDK payload for in-app payment
sdk_payload = payment["sdk_payload"]["payload"]
client_auth = sdk_payload["clientAuthToken"]
amount = sdk_payload["amount"]  # "32.0"
```

### Step 5: Payment (Juspay Integration)

```python
# Option 1: Redirect to web payment page
import webbrowser
webbrowser.open(web_url)

# Option 2: Use Juspay SDK (in-app)
# The sdk_payload contains all needed parameters:
# - clientAuthToken: Authentication token for Juspay
# - orderId: Order identifier
# - amount: Payment amount
# - merchantId: "nammayatriBAP"
# - service: "in.juspay.hyperpay"
# - returnUrl: Callback URL after payment
```

### Step 6: Check Ticket

```python
# After payment, check booking status again
response = requests.get(
    f"{BASE}/frfs/booking/{booking_id}/status",
    params={
        "city": "chennai",
        "vehicleType": '"METRO"'
    },
    headers={"token": TOKEN}
)

data = response.json()
if data["status"] == "CONFIRMED":
    tickets = data["tickets"]
    # Each ticket has QR data for metro gate scanning
```

## Quote Response Details

```json
{
  "_type": "SingleJourney",
  "categories": [
    {
      "categoryName": "ADULT",
      "categoryPrice": {"amount": 32, "currency": "INR"},
      "categoryOfferedPrice": {"amount": 32, "currency": "INR"},
      "categorySelectedQuantity": 1,
      "categoryMeta": {
        "code": "ADULT",
        "description": "Standard ticket category for adult passengers",
        "title": "ADULT",
        "tnc": "Standard terms and conditions apply"
      }
    }
  ],
  "price": 32,
  "quoteId": "...",
  "stations": [
    {"name": "Vadapalani", "stationType": "START"},
    {"name": "AG-DMS", "stationType": "END"}
  ],
  "validTill": "2026-03-30T08:50:28.902Z",
  "vehicleType": "METRO"
}
```

## Booking Response Details

```json
{
  "_type": "SingleJourney",
  "bookingId": "f1f4adda-00de-4e29-adb3-992658039cc6",
  "city": "std:044",
  "createdAt": "2026-03-30T09:05:49.562Z",
  "isFareChanged": false,
  "payment": null,
  "price": 32,
  "quantity": 1,
  "status": "NEW",
  "tickets": [],
  "validTill": "2026-03-30T09:35:49.569Z",
  "vehicleType": "METRO"
}
```

## Payment Status Response Details

```json
{
  "bookingId": "f1f4adda-...",
  "status": "PAYMENT_PENDING",
  "price": 32,
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
    },
    "status": "NEW"
  },
  "tickets": []
}
```

## Error Handling

### Search Fails
- Check token validity
- Verify station codes exist
- Check vehicleType encoding

### Quote Empty
- Wait longer (up to 5 seconds)
- Re-initiate search
- Check station codes are valid for metro

### Confirm Fails
- Quote may have expired
- Re-initiate search and quote
- Check quantity is valid

### Payment Fails
- Check clientAuthToken not expired
- Verify order_id is valid
- Check amount matches quote

## Fare Structure

Based on observed data:
- Vadapalani to AG-DMS: 32 INR (single), 64 INR (return)
- Fare is distance-based
- Same fare for all times of day
- No peak/off-peak pricing observed
- Adult category only (no child/senior observed)

## Timing

- Quote generation: 2-3 seconds
- Quote validity: ~30 minutes
- Payment token validity: ~15 minutes
- Booking validity: ~30 minutes
