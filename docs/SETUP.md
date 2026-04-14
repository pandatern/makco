# Setup Guide

## Prerequisites

- Nim 2.2.8+
- Prologue 0.6.8
- nginx
- Git
- choosenim (for Nim version management)

## Quick Start

```bash
# Clone the repository
git clone https://github.com/pandatern/makco.git
cd makco/backend/src

# Compile (needs SSL for MovingTech API)
nim c -d:ssl -d:release -o:makco makco.nim

# Run locally
./makco

# Or use nginx (already configured)
# https://api.pandatern.tech/
```

## Framework

Currently using **Prologue** (0.6.8). Alternatives tested:
- httpbeast 0.4.1 - API incompatibilities with Nim 2.2.8
- jester 0.6.0 - response handling issues
- httpx - too low-level like httpbeast
- GuildenStern - untested

## Backend

### Compile

```bash
cd backend/src
nim c -d:ssl -d:release -o:makco makco.nim
```

### Run with nginx proxy

```bash
# Backend runs on port 8080
# nginx proxies https://api.pandatern.tech -> localhost:8080
nginx -s reload  # after backend restart
```

### Admin Token

Payment skips only for exact token: `admin_token_6374746721`

### API Endpoints

- Health: GET /
- Auth: POST /auth, POST /auth/{authId}/verify
- Metro: GET /metro/stations, GET /metro/routes
- Search: POST /metro/search, GET /metro/search/{searchId}/quote
- Booking: POST /metro/quote/{quoteId}/confirm
- Status: GET /booking/{bookingId}/status, GET /booking/{bookingId}/refresh
- Profile: GET /profile
- Tickets: GET /tickets

### Run

```bash
./makco
# Listens on port 8080 by default
```

### nginx Configuration

The nginx config for `api.pandatern.tech` is at `/etc/nginx/sites-available/pandatern_api`:

```nginx
server {
    listen 80;
    listen 443 ssl;
    server_name api.pandatern.tech;
    
    ssl_certificate /etc/letsencrypt/live/pandacloud.me-0001/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/pandacloud.me-0001/privkey.pem;
    
    location / {
        proxy_pass http://localhost:8080;
    }
}
```

## API Testing

### Test Authentication

```bash
# Step 1: Get auth ID (sends OTP to phone)
curl -X POST "https://api.pandatern.tech/auth" \
  -H "Content-Type: application/json" \
  -d '{"mobileNumber":"YOUR_PHONE"}'

# Step 2: Verify OTP
curl -X POST "https://api.pandatern.tech/auth/{authId}/verify" \
  -H "Content-Type: application/json" \
  -d '{"otp":"YOUR_4_DIGIT_OTP"}'
```

### Test with Token

```bash
# Use token from verify response
curl -H "token: YOUR_TOKEN" https://api.pandatern.tech/metro/stations
```

## Android Build

Requirements:
- Android SDK 35+
- Gradle 8.5+
- Java 17+

```bash
cd frontend/android
./gradlew assembleDebug
# or
gradle assembleDebug
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| DB_PATH | makco.db | SQLite database |
| JWT_SECRET | makco-secret-key | JWT signing key |

## Common Issues

### "INVALID_TOKEN" Error

This comes from MovingTech API - you need a valid token from OTP verification.

### Prologue API Errors

If you see `undeclared identifier: 'ctx'` in compilation, ensure you're using the correct jsonResponse call with ctx parameter.