# Architecture Guide

## System Overview

MetroGo is a standalone metro booking application that leverages the MovingTech/Namma Yatri transport API platform for backend services. We do not run our own metro infrastructure. We use theirs.

## Architecture Diagram

```
[User Phone]
    |
    v
[MetroGo PWA Frontend]
    |
    v
[MetroGo Backend (FastAPI)]  <-- Optional, for our own logic
    |
    v
[MovingTech API]  <-- Their backend, our data source
    |
    +---> /auth (OTP authentication)
    +---> /frfs/* (Metro routes, stations, search, booking)
    +---> /profile (User data)
    +---> /ticket/* (Ticket management)
    |
    v
[Juspay Payment Gateway]
    |
    v
[UPI / Card / Wallet]
```

## Data Flow

### 1. Authentication
```
User -> Phone Number -> /auth -> OTP Sent
User -> OTP Code -> /auth/{id}/verify -> Session Token
```

### 2. Metro Search
```
User -> Select Stations -> /frfs/search -> searchId
App -> Poll Quote -> /frfs/search/{id}/quote -> Fare + quoteId
```

### 3. Booking
```
User -> Confirm -> /frfs/quote/{quoteId}/confirm -> bookingId
App -> Check Status -> /frfs/booking/{id}/status -> Payment Order
User -> Pay -> Juspay Payment Page -> Ticket Generated
```

## Component Breakdown

### Frontend (PWA)
- Station picker (search, nearby)
- Route visualization (metro map)
- Fare display
- Booking confirmation
- Payment redirect
- QR ticket display

### Backend (FastAPI - Optional)
- Can proxy their API for our own token management
- Can cache station/route data
- Can add our own features on top
- Can handle payment callbacks

### Their API (MovingTech)
- OTP authentication
- Metro station data
- Route data
- Fare calculation
- Booking management
- Payment order creation
- Ticket generation

### Payment (Juspay)
- Payment page rendering
- UPI deep links
- Card processing
- Transaction status
- Webhook callbacks

## Key Design Decisions

### Why Use Their API
- Real-time station data
- Accurate fare calculation
- Official payment integration
- QR ticket generation
- No need to build metro infrastructure

### Why Build Our Own Frontend
- Control user experience
- Add features they don't have
- Different branding
- Custom analytics
- Multi-city support

### Why Optional Backend
- Their API can be called directly from frontend
- Our backend adds proxy layer for security
- Can cache responses
- Can add rate limiting
- Can handle payment callbacks

## Data Sources

### From Their API (Live)
- Station list (41 stations)
- Route list (4 routes)
- Fare prices
- Booking creation
- Payment orders

### From OTA Bundles (Cached)
- App configuration
- Feature flags
- City configs
- Vehicle types

### From Firebase Remote Config (Cached)
- 170 config entries
- Feature flags
- UI configuration
- Support contacts

### Seeded Locally
- Station coordinates (backup)
- Route mappings (backup)
- Fare structure (reference)
