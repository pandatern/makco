# Makco - Chennai Metro Booking App

A standalone metro ticketing application that integrates with the Chennai Metro (CMRL) infrastructure through the MovingTech/Namma Yatri transport API platform.

## What This Is

This project reverse-engineers and documents the complete Chennai One / Namma Yatri metro booking API. We use their authentication, their data, and their payment infrastructure to build our own metro ticketing app.

## Status

- **Backend**: Running at `https://api.pandatern.tech/`
- **Auth**: Working (phone OTP based, 4-digit)
- **Metro Data**: 41 stations, 4 routes (Blue/Green lines)
- **Search/Quote**: Working (live fare pricing)
- **Booking**: Working (creates booking, returns Juspay payment order)
- **Payment**: Juspay integration (production)
- **Android App**: Complete (neo-brutalist design)

## Quick Start

```bash
# Clone the repo
git clone https://github.com/pandatern/makco.git
cd makco

# Backend runs on port 8080
./backend/src/makco

# Or use nginx (already configured)
# https://api.pandatern.tech/
```

## Tech Stack

- **Backend**: Nim 2.0.8 + Prologue (httpbeast)
- **Android**: Kotlin + Jetpack Compose
- **Auth**: JWT + OTP (phone-based MovingTech)
- **Payments**: Juspay (UPI, cards, wallet)
- **Deployment**: nginx with SSL

## Project Structure

```
makco/
├── backend/               # Nim backend
│   └── src/
│       ├── makco.nim      # Main entry
│       ├── handlers.nim    # HTTP handlers
│       ├── api_client.nim  # MovingTech proxy
│       └── config.nim     # Configuration
├── frontend/android/       # Android app
│   └── app/src/main/
│       └── java/com/pandatern/makco/
│           ├── data/      # API, models
│           └── ui/        # Screens, theme
└── docs/                 # Documentation
```

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `/` | Health check |
| `/auth` | OTP init |
| `/auth/{id}/verify` | OTP verify |
| `/metro/stations` | Get all stations |
| `/metro/routes` | Get all routes |
| `/metro/search` | Search fares |
| `/metro/quote` | Get quote |
| `/metro/confirm` | Confirm booking |
| `/booking/{id}/status` | Booking status |
| `/profile` | User profile |
| `/tickets` | Ticket history |

## Documentation

- [API Reference](docs/API_REFERENCE.md) - Complete API endpoint documentation
- [Architecture](docs/ARCHITECTURE.md) - System architecture and data flow
- [Authentication Flow](docs/AUTH_FLOW.md) - OTP-based auth implementation
- [Booking Flow](docs/BOOKING_FLOW.md) - End-to-end booking pipeline
- [Payment Integration](docs/PAYMENT.md) - Juspay payment gateway details
- [Data Models](docs/DATA_MODELS.md) - Station, route, booking schemas
- [Security Analysis](docs/SECURITY.md) - Security posture assessment

## License

For internal development use only.