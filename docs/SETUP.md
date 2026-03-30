# Setup Guide

## Prerequisites

- Python 3.10+
- pip3
- Git

## Quick Start

```bash
# Clone the repository
git clone https://github.com/pandatern/makco.git
cd makco

# Install dependencies
pip3 install fastapi uvicorn sqlalchemy pydantic python-jose passlib bcrypt pyotp qrcode pillow aiofiles python-multipart

# Test API connectivity
python3 scripts/test_api.py
```

## API Testing

### Test Authentication

```bash
# Step 1: Get auth ID (sends OTP to phone)
curl -X POST "https://api.moving.tech/pilot/app/v2/auth" \
  -H "Content-Type: application/json" \
  -d '{"mobileNumber":"YOUR_PHONE","mobileCountryCode":"+91","merchantId":"NAMMA_YATRI","clientId":"NAMMA_YATRI"}'

# Step 2: Verify OTP
curl -X POST "https://api.moving.tech/pilot/app/v2/auth/{authId}/verify" \
  -H "Content-Type: application/json" \
  -d '{"otp":"YOUR_4_DIGIT_OTP","deviceToken":"test_token"}'

# Save the token from response
export METRO_TOKEN="your-token-here"
```

### Test Metro Data

```bash
# Get stations
curl -H "token: $METRO_TOKEN" \
  'https://api.moving.tech/pilot/app/v2/frfs/stations?city=chennai&vehicleType=%22METRO%22'

# Get routes
curl -H "token: $METRO_TOKEN" \
  'https://api.moving.tech/pilot/app/v2/frfs/routes?city=chennai&vehicleType=%22METRO%22'
```

### Test Booking Flow

```bash
# Step 1: Search
SEARCH_ID=$(curl -s -H "token: $METRO_TOKEN" \
  'https://api.moving.tech/pilot/app/v2/frfs/search?city=chennai&vehicleType=%22METRO%22' \
  -H "Content-Type: application/json" \
  -d '{"fromStationCode":"SVA|0225","toStationCode":"SGM|0115","quantity":1}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['searchId'])")

# Step 2: Wait and get quote
sleep 2
QUOTE_ID=$(curl -s -H "token: $METRO_TOKEN" \
  "https://api.moving.tech/pilot/app/v2/frfs/search/$SEARCH_ID/quote?city=chennai&vehicleType=%22METRO%22" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)[0]['quoteId'])")

# Step 3: Confirm booking
BOOKING_ID=$(curl -s -H "token: $METRO_TOKEN" \
  "https://api.moving.tech/pilot/app/v2/frfs/quote/$QUOTE_ID/confirm?city=chennai&vehicleType=%22METRO%22" \
  -H "Content-Type: application/json" \
  -d '{"quantity":1}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['bookingId'])")

# Step 4: Get payment details
curl -s -H "token: $METRO_TOKEN" \
  "https://api.moving.tech/pilot/app/v2/frfs/booking/$BOOKING_ID/status?city=chennai&vehicleType=%22METRO%22" \
  | python3 -m json.tool
```

## Environment Variables

```bash
# Required for auth
export METRO_PHONE="your_phone_number"
export METRO_TOKEN="your_session_token"

# Optional for payment
export JUSPAY_ENV="production"  # or "sandbox"
```

## Project Structure

```
makco/
  README.md               # Main README
  docs/                    # Documentation
    API_REFERENCE.md       # Complete API docs
    ARCHITECTURE.md        # System architecture
    AUTH_FLOW.md           # Authentication guide
    BOOKING_FLOW.md        # Booking pipeline
    PAYMENT.md             # Payment integration
    DATA_MODELS.md         # Data schemas
    SECURITY.md            # Security analysis
    SETUP.md               # This file
  src/                     # Source code
  scripts/                 # Utility scripts
  tests/                   # Test suite
```

## Running the Backend (Optional)

```bash
cd src
python3 -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```

API docs available at: http://localhost:8000/docs

## Troubleshooting

### "INVALID_TOKEN" Error
- Token expired or invalid
- Re-authenticate with phone OTP
- Check token is in header, not query param

### "Query parameter vehicleType is required"
- vehicleType must be JSON-encoded in URL
- Use: `vehicleType=%22METRO%22`
- Not: `vehicleType=METRO`

### "Not found" on Booking Endpoint
- Correct endpoint: `/frfs/quote/{quoteId}/confirm`
- NOT `/frfs/book` or `/frfs/booking`

### Quote Returns Empty Array
- Wait 2-3 seconds after search
- Check searchId is valid
- Verify station codes exist

### Payment Status Shows INTERNAL_ERROR
- Payment methods endpoint is broken on their side
- Payment order is generated during booking status check
- Use the payment data from booking status response
