# AGENTS.md — How to work with the Makco repository

Purpose: give future automated agents the non-obvious, repo-specific knowledge they need to operate effectively (build, test, modify, triage) without trial-and-error.

---

## Quick summary

- Backend is a Nim service (entry: backend/src/makco.nim). It proxies/consumes the MovingTech (Namma Yatri) API and exposes a small REST API.
- Frontend is an Android app under frontend/android (Gradle). This guide focuses on backend/DevOps tasks.
- Primary external dependencies: MovingTech API and Juspay for payments. There are live endpoints and a test script that calls the real MovingTech API.

---

## Essential commands (observed in repo)

- Build backend (local):
  - cd backend/src && nim c -d:ssl -d:release -o:makco makco.nim
    - See docs/SETUP.md and backend/makco.nimble
- Run backend (local):
  - ./backend/src/makco  (binary produced by compile)
- Install Nim dependencies (CI uses nimble):
  - working-directory: backend && nimble install -y
- CI build (GitHub Actions): .github/workflows/build-backend.yml compiles with nim c -d:ssl -d:release (artifact uploaded)
- Android build (docs):
  - cd frontend/android && ./gradlew assembleDebug
- API test script (calls live MovingTech API):
  - ./scripts/test_api.py  (interactive; sends real OTPs unless you skip auth)

Note: There is no Makefile. Do not invent make targets.

---

## Project layout and key files

- backend/
  - makco.nimble  (nimble package; requires prologue)
  - src/
    - makco.nim — main entry and route registrations (defines endpoints). See backend/src/makco.nim:5-31
    - handlers.nim — request handlers and input validation (token header checks; OTP length, station/quote checks). See backend/src/handlers.nim (handlers and validation logic). Example: getStations token check at handlers.nim:55-66
    - api_client.nim — MovingTech API client wrappers (mtAuth, mtVerifyAuth, mtGetStations, etc.). Contains makeRequest helper and notes about vehicleType query param. See backend/src/api_client.nim
    - config.nim — configuration defaults and getEnv helper (env-driven overrides). See backend/src/config.nim
- docs/ — rich project documentation (ARCHITECTURE.md, API_REFERENCE.md, SETUP.md, etc.) — read these first for protocol details and gotchas.
- scripts/test_api.py — end-to-end test script that uses the live MovingTech API. It demonstrates important request shapes and headers (vehicleType must be JSON-encoded). See scripts/test_api.py
- frontend/android/ — Android app (Gradle). Not covered in detail here; docs/SETUP.md contains Android requirements.

---

## Architecture & control/data flow (concise)

- The backend proxies MovingTech endpoints and adds small server-side validation and convenience. Requests from client -> this backend -> MovingTech -> (Juspay when booking requires payment) -> client.
- Handlers validate inputs, require `token` header for transport endpoints, and call corresponding mt* functions in api_client.nim which centralize HTTP calls.

---

## Naming conventions and code patterns (observed)

- Exported procs use `*` suffix (Nim style). Many handlers are exported: health*, initAuth*, etc. (handlers.nim)
- MovingTech wrappers use `mt` prefix: mtAuth, mtVerifyAuth, mtGetStations, mtSearchFare, mtGetQuote, mtConfirmBooking, mtGetBookingStatus, mtGetProfile, mtGetTicketBookings (api_client.nim).
- Constants are UPPERCASE (BASE_URL, MERCHANT_ID, VEHICLE_TYPE) in api_client.nim and config.nim.
- HTTP handlers use Prologue's Context and async handlers; response helper jsonResponse exists in handlers.nim:5-8.

---

## Testing approach and patterns

- There are no unit tests in the repo. The provided end-to-end test is scripts/test_api.py which targets the live MovingTech API and may send real OTPs.
- Use the test script to exercise flows (health, auth, stations, routes, search, quote, booking). It is interactive and expects either a real phone OTP or a pre-existing token.
- CI builds the backend binary and uploads it as an artifact (no automated API tests in CI).

---

## Important gotchas & non-obvious details (must-read)

- vehicleType query value must be JSON-encoded (escaped quotes) in query strings. This repo uses vehicleType=%22METRO%22 (see API_REFERENCE.md lines that emphasize this and api_client.nim uses VEHICLE_TYPE = "\"METRO\""). If you call endpoints with vehicleType=METRO (no quotes) the MovingTech API will not behave as documented. See docs/API_REFERENCE.md:131-134 and backend/src/api_client.nim:8.

- OTP length is 4 digits (not 6). Handlers validate otp.len == 4 (handlers.nim:42-44). The test script expects 4-digit OTPs (scripts/test_api.py:245).

- Token header is required for most endpoints. Handlers check for token and return an HTTP401 with errorCode "MISSING_HEADER" (many handlers, e.g., handlers.nim:56-63). When testing, pass header `token: <session-token>` from verify response.

- Backend builds require SSL flags for Nim (nim c -d:ssl ...) because HTTP client code touches secure endpoints. The CI and docs both compile with -d:ssl. See docs/SETUP.md and .github/workflows/build-backend.yml:33-34.

- Default secrets and configs in config.nim are placeholders (JWT_SECRET, FIREBASE_API_KEY, JUSPAY_*). Treat them as non-production defaults. See backend/src/config.nim lines ~35-46 and 21-27.

- scripts/test_api.py interacts with the live MovingTech production-ish pilot endpoint (BASE_URL = https://api.moving.tech/pilot/app/v2). Do not run it without understanding it will send OTPs and may create bookings.

- Prologue vs httpbeast: repo currently uses Prologue. docs/SETUP.md notes httpbeast API incompatibilities. If you attempt to swap frameworks, read the note at docs/SETUP.md:27-33.

- TLS cert paths in config.nim default to certs/server.crt and certs/server.key — runtime will expect these if TLS is used. See backend/src/config.nim:11-13.

---

## Environment variables (observed defaults)

- DB_PATH (default: makco.db) — config.nim:40
- JWT_SECRET (default: makco-secret-key-change-in-production) — config.nim:36
- REDIS_* defaults (disabled) — config.nim:42-46
- TLS_CERT, TLS_KEY defaults (certs/server.crt, certs/server.key) — config.nim:12-13

Agents should read backend/src/config.nim before assuming env names or formats.

---

## CI and build notes

- GitHub Actions for backend installs Nim via choosenim script and runs `nimble install -y` in backend directory, then compiles backend/src/makco.nim with -d:ssl -d:release. See .github/workflows/build-backend.yml:22-35.
- The nimble package declares `requires "nim >= 2.0.0"` and `requires "prologue >= 0.6.0"` (backend/makco.nimble). Use compatible versions when reproducing CI locally.

---

## Where to read first (recommended discovery order for agents)

1. docs/SETUP.md — build and run steps and common issues.
2. docs/API_REFERENCE.md — endpoint shapes, headers, vehicleType gotcha, OTP length, booking status values.
3. backend/src/makco.nim — route registration and main() usage (quick view of surface API).
4. backend/src/handlers.nim — validation logic and error shapes (see token checks and OTP validations).
5. backend/src/api_client.nim — how requests to MovingTech are composed, makeRequest implementation, param encoding.
6. backend/src/config.nim — env defaults and constants.

---

## Safe-to-automate actions and cautions

Safe automation:
- Building the backend binary with documented flags.
- Running static edits to handlers and api_client if you follow existing patterns (exported procs, mt* prefixes).
- Running non-destructive tests such as GET /metro/stations using a valid token.

Cautions (do not automate without explicit user consent):
- Sending real OTPs or performing bookings via scripts/test_api.py (it sends SMS OTPs and may create bookings/payments).
- Changing default secrets in config.nim and committing them to repo; avoid exposing real credentials.

---

## Useful quick references (file:line)

- backend/src/makco.nim:5-31 — main, route registrations
- backend/src/handlers.nim:5-14 — jsonResponse helper; handlers.nim:16-28 initAuth flow
- backend/src/handlers.nim:55-66 — getStations token validation
- backend/src/handlers.nim:42-44 — OTP length validation in verifyAuth
- backend/src/api_client.nim:1-9 — constants including VEHICLE_TYPE
- backend/src/config.nim:36-41 — JWT_SECRET, DB_PATH defaults
- backend/makco.nimble:10-11 — required Nim and prologue versions
- .github/workflows/build-backend.yml:22-35 — CI install & build steps
- scripts/test_api.py:12-14, 231-250 — test script and interactive auth flow

---

## If something is missing

- There are no unit tests or a test runner configuration. If you need unit tests, add a test directory and test runner config, and update CI accordingly.
- If you need more runtime wiring (database, Redis), config.nim contains placeholders; implement/extend as needed.

---

Maintain this file when you discover new non-obvious conventions, CI changes, or environment requirements.
