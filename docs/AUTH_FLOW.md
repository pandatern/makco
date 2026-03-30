# Authentication Flow

## Overview

The Chennai Metro API uses phone-based OTP authentication. No email, no social login, no API keys for end users. Just phone number + 4-digit OTP.

## Flow Diagram

```
1. User enters phone number
        |
        v
2. POST /auth
   Body: {mobileNumber, mobileCountryCode, merchantId, clientId}
        |
        v
3. Response: {authId, attempts: 3, authType: "OTP"}
   + SMS OTP sent to phone (4 digits)
        |
        v
4. User enters OTP
        |
        v
5. POST /auth/{authId}/verify
   Body: {otp: "1234", deviceToken: "xxx"}
        |
        v
6. Response: {token: "uuid-token", userId: "uuid"}
        |
        v
7. Use token on all subsequent requests
   Header: token: <your-token>
```

## Implementation

### Step 1: Initiate Auth

```python
import requests

response = requests.post(
    "https://api.moving.tech/pilot/app/v2/auth",
    json={
        "mobileNumber": "9876543210",
        "mobileCountryCode": "+91",
        "merchantId": "NAMMA_YATRI",
        "clientId": "NAMMA_YATRI"
    }
)

data = response.json()
auth_id = data["authId"]
# OTP sent to phone
```

### Step 2: Verify OTP

```python
response = requests.post(
    f"https://api.moving.tech/pilot/app/v2/auth/{auth_id}/verify",
    json={
        "otp": "1234",  # 4-digit OTP from SMS
        "deviceToken": "your_device_token_here"
    }
)

data = response.json()
token = data["token"]
user_id = data["userId"]
```

### Step 3: Use Token

```python
response = requests.get(
    "https://api.moving.tech/pilot/app/v2/frfs/stations",
    params={
        "city": "chennai",
        "vehicleType": '"METRO"'  # JSON-encoded string
    },
    headers={
        "token": token
    }
)
```

## Important Details

### OTP Format
- 4 digits (NOT 6)
- Regex validation: `^[0-9]*$`
- Length validation: exactly 4 characters
- 3 attempts per auth session
- Re-initiate auth for fresh attempts

### merchantId / clientId
- Use `"NAMMA_YATRI"` for both
- This is the generic consumer app identifier
- Not app-specific, not verified against client

### Token Format
- UUID format: `29b7452c-5bb4-463e-97da-44c6eda3b37e`
- No expiration observed (persistent)
- No refresh mechanism needed
- Invalid token returns `INVALID_TOKEN` error

### deviceToken
- Required field in verify request
- Can be any string value
- Not validated server-side
- Recommended: generate random string

### No Client Verification
- No app signature check
- No device fingerprint validation
- No HMAC signing required
- Token is token, works from any client

## Error Handling

### Wrong OTP
```json
{
  "errorCode": "INVALID_AUTH_DATA",
  "errorMessage": null,
  "errorPayload": null
}
```
HTTP 400. Remaining attempts decrease.

### Invalid Request Format
```json
{
  "errorCode": "REQUEST_VALIDATION_FAILURE",
  "errorPayload": [
    {
      "expectation": "(length(otp) == 4 and otp matches regex /^[0-9]*$/)",
      "fieldName": ["otp"]
    }
  ]
}
```
HTTP 400. Fix request format.

### No Auth Request Found
```json
{
  "errorCode": "INVALID_AUTH_DATA"
}
```
HTTP 400. Auth session expired or invalid authId.

## Session Management

- Store token in secure storage
- Token persists across app restarts
- No token refresh needed
- Logout: clear stored token
- Multiple devices: each needs own auth flow
