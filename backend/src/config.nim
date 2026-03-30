import os

const
  APP_NAME* = "Makco"
  APP_VERSION* = "1.0.0"
  SERVER_PORT* = 8080
  SERVER_HOST* = "0.0.0.0"

  # MovingTech API
  MT_BASE_URL* = "https://api.moving.tech/pilot/app/v2"
  MT_AUTH_URL* = MT_BASE_URL & "/auth"
  MT_FRFS_URL* = MT_BASE_URL & "/frfs"
  MT_PROFILE_URL* = MT_BASE_URL & "/profile"

  # Firebase
  FIREBASE_API_KEY* = "AIzaSyD5iVZHCLDX5qZG-3v0rG5bbhA-Q8BmHkM"

  # Juspay
  JUSPAY_MERCHANT* = "nammayatriBAP"
  JUSPAY_CLIENT* = "nammayatriBAP"
  JUSPAY_ENV* = "production"

  # Metro
  DEFAULT_CITY* = "chennai"
  DEFAULT_VEHICLE* = "METRO"
  MERCHANT_ID* = "NAMMA_YATRI"
  CLIENT_ID* = "NAMMA_YATRI"

  # JWT
  JWT_SECRET* = getEnv("JWT_SECRET", "makco-secret-key-change-in-production")
  JWT_EXPIRY_HOURS* = 24

  # Database
  DB_PATH* = getEnv("DB_PATH", "makco.db")

proc getEnv*(key, default: string): string =
  result = os.getEnv(key)
  if result.len == 0:
    result = default
