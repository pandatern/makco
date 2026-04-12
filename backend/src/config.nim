import os

proc parseInt*(s: string): int

const
  APP_NAME* = "Makco"
  APP_VERSION* = "1.0.0"
  SERVER_PORT* = 8080
  SERVER_HOST* = "0.0.0.0"

  # TLS - set via environment
  TLS_CERT* = getEnv("TLS_CERT", "")
  TLS_KEY* = getEnv("TLS_KEY", "")

  # MovingTech API
  MT_BASE_URL* = getEnv("MT_BASE_URL", "https://api.moving.tech/pilot/app/v2")
  MT_AUTH_URL* = MT_BASE_URL & "/auth"
  MT_FRFS_URL* = MT_BASE_URL & "/frfs"
  MT_PROFILE_URL* = MT_BASE_URL & "/profile"

  # Firebase - set via environment
  FIREBASE_API_KEY* = getEnv("FIREBASE_API_KEY", "")

  # Juspay - set via environment
  JUSPAY_MERCHANT* = getEnv("JUSPAY_MERCHANT", "")
  JUSPAY_CLIENT* = getEnv("JUSPAY_CLIENT", "")
  JUSPAY_ENV* = getEnv("JUSPAY_ENV", "production")

  # Metro
  DEFAULT_CITY* = "chennai"
  DEFAULT_VEHICLE* = "METRO"
  MERCHANT_ID* = getEnv("MERCHANT_ID", "NAMMA_YATRI")
  CLIENT_ID* = getEnv("CLIENT_ID", "NAMMA_YATRI")

  # JWT - set via environment
  JWT_SECRET* = getEnv("JWT_SECRET", "")
  JWT_EXPIRY_HOURS* = 24

  # Database
  DB_PATH* = getEnv("DB_PATH", "makco.db")

  # Redis (future)
  REDIS_HOST* = getEnv("REDIS_HOST", "127.0.0.1")
  REDIS_PORT* = 6379
  REDIS_DB* = 0
  REDIS_ENABLED* = false

proc getEnv*(key, default: string): string =
  result = os.getEnv(key)
  if result.len == 0:
    result = default

proc parseInt*(s: string): int =
  result = 0
  for c in s:
    if c in {'0'..'9'}:
      result = result * 10 + (ord(c) - ord('0'))