# Makco - Chennai Metro Booking App

A standalone metro ticketing application that integrates with the Chennai Metro (CMRL) infrastructure through the MovingTech/Namma Yatri transport API platform.

## What This Is

This project reverse-engineers and documents the complete Chennai One / Namma Yatri metro booking API. We use their authentication, their data, and their payment infrastructure to build our own metro ticketing app.

## Status

- **Auth**: Working (phone OTP based, 4-digit)
- **Metro Data**: 41 stations, 4 routes (Blue/Green lines)
- **Search/Quote**: Working (live fare pricing)
- **Booking**: Working (creates booking, returns Juspay payment order)
- **Payment**: Juspay integration (production)
- **Tickets**: QR code generation ready

## Quick Start

```bash
# Clone the repo
git clone https://github.com/pandatern/makco.git
cd makco

# Read the docs
cat docs/API_REFERENCE.md
cat docs/AUTH_FLOW.md
cat docs/BOOKING_FLOW.md
```

## Documentation

- [API Reference](docs/API_REFERENCE.md) - Complete API endpoint documentation
- [Architecture](docs/ARCHITECTURE.md) - System architecture and data flow
- [Authentication Flow](docs/AUTH_FLOW.md) - OTP-based auth implementation
- [Booking Flow](docs/BOOKING_FLOW.md) - End-to-end booking pipeline
- [Payment Integration](docs/PAYMENT.md) - Juspay payment gateway details
- [Data Models](docs/DATA_MODELS.md) - Station, route, booking schemas
- [Security Analysis](docs/SECURITY.md) - Security posture assessment
- [Setup Guide](docs/SETUP.md) - Development environment setup

## Project Structure

```
makco/
  docs/                    # Documentation
  src/                     # Source code
    app/                   # FastAPI application
      api/                 # API endpoints
      models/              # Data models
      core/                # Config, database, security
      services/            # Business logic
    data/                  # Seed data, metro stations
    frontend/              # PWA frontend
  scripts/                 # Utility scripts
  tests/                   # Test suite
```

## Tech Stack

- **Backend**: Python 3.10 + FastAPI
- **Database**: SQLite (dev) / PostgreSQL (prod)
- **Auth**: JWT + OTP (phone-based)
- **Payments**: Juspay (UPI, cards, wallet)
- **Frontend**: PWA (HTML/CSS/JS)

## License

For internal development use only.
