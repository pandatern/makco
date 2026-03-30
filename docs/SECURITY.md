# Security Analysis

## Security Posture

The Chennai One / Namma Yatri API has a specific security model. This document analyzes what's open, what's locked, and what that means for our integration.

## Authentication Model

### How Auth Works
1. Phone number + OTP (4-digit SMS)
2. Firebase handles SMS delivery
3. Backend verifies Firebase token
4. Backend issues its own session token
5. Session token used on all API calls

### What's Protected
- All transport API endpoints (FRFS, Beckn, multimodal)
- User data endpoints
- Payment processing

### What's NOT Protected
- Firebase Remote Config (API key only)
- OTA bundle downloads (no auth)
- Juspay config downloads (no auth)
- Log/analytics endpoint (no auth)

## Client Verification

### What They DON'T Check
- App signature (no signing verification)
- Device fingerprint (no device binding)
- Client type (no distinction between their app and ours)
- HMAC signing (no request signing)
- User-Agent (no header validation)
- Origin/CORS (not enforced for API calls)

### What This Means
- Any HTTP client can use their API
- Our token works identically to their app's token
- They cannot distinguish our requests from theirs
- No technical barrier to integration

## Token Security

### Token Format
- UUID format
- No JWT structure
- No expiration observed
- No refresh mechanism

### Token Usage
- Sent as plain header: `token: <value>`
- Not Bearer auth
- No additional signatures
- Works from any origin

## Data Exposure

### Publicly Accessible (No Auth)
- 170 Firebase Remote Config entries (933KB)
- OTA bundles for all 4 app variants (~17MB each)
- Juspay payment gateway config
- App feature flags
- City configurations
- Vehicle types per city

### Accessible With Token
- 41 metro stations with coordinates
- 4 metro routes
- Fare prices for any station pair
- User profile data
- Booking history

### NOT Accessible
- Other users' data
- Admin endpoints
- Payment processing without booking
- Driver data (cab module)

## Attack Surface

### What's Exploitable
- Log injection (analytics endpoint accepts any payload)
- OTA bundle enumeration (can track version changes)
- Firebase config manipulation (if writable - untested)

### What's NOT Exploitable
- Booking without payment (payment is separate)
- Token theft (tokens are user-specific)
- Data exfiltration (no bulk data endpoints)
- Service disruption (rate limiting observed)

## Our Security Measures

### Recommended
- Store tokens in secure storage (not localStorage)
- Use HTTPS for all API calls
- Validate all API responses
- Handle token expiration gracefully
- Don't log tokens in analytics

### For Production
- Add our own proxy layer
- Implement rate limiting
- Add request validation
- Monitor for anomalies
- Use environment variables for config

## Legal Considerations

### What We're Doing
- Using a public API with valid authentication
- Making requests their API accepts
- Processing payments through their gateway

### What We're NOT Doing
- Breaking authentication
- Bypassing security controls
- Accessing unauthorized data
- Denying service to others
- Distributing their code

## Risk Assessment

### Low Risk
- Using their auth system (they designed it to work with tokens)
- Reading station/route data (publicly accessible with token)
- Creating bookings (normal API usage)

### Medium Risk
- Payment integration (using their Juspay merchant)
- Token persistence (if token gets compromised)

### High Risk
- None identified for normal usage

## Recommendations

1. Use their API as intended
2. Don't abuse rate limits
3. Handle errors gracefully
4. Don't redistribute their source code
5. Respect their terms of service
6. Build value on top, don't just copy
