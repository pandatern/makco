import os

const
  APP_NAME* = "Makco"
  APP_VERSION* = "1.0.0"
  SERVER_PORT* = 8443
  SERVER_HOST* = "0.0.0.0"

  # TLS
  TLS_CERT* = getEnv("TLS_CERT", "certs/server.crt")
  TLS_KEY* = getEnv("TLS_KEY", "certs/server.key")

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

  # Redis (future)
  REDIS_HOST* = getEnv("REDIS_HOST", "127.0.0.1")
  REDIS_PORT* = parseInt(getEnv("REDIS_PORT", "6379"))
  REDIS_DB* = parseInt(getEnv("REDIS_DB", "0"))
  REDIS_ENABLED* = getEnv("REDIS_ENABLED", "false") == "true"

proc getEnv*(key, default: string): string =
  result = os.getEnv(key)
  if result.len == 0:
    result = default

proc parseInt*(s: string): int =
  result = 0
  for c in s:
    if c in {'0'..'9'}:
      result = result * 10 + (ord(c) - ord('0'))
