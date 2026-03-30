#!/usr/bin/env python3
"""
MetroGo - API Test Script
Tests all confirmed working endpoints against the Chennai Metro API.
"""

import requests
import json
import time
import sys

BASE_URL = "https://api.moving.tech/pilot/app/v2"


def test_health():
    """Test API health endpoint."""
    print("=" * 50)
    print("TEST: API Health Check")
    print("=" * 50)
    try:
        resp = requests.get(f"{BASE_URL}")
        print(f"  Status: {resp.status_code}")
        print(f"  Response: {resp.text}")
        return resp.status_code == 200
    except Exception as e:
        print(f"  ERROR: {e}")
        return False


def test_auth(phone):
    """Test auth flow - sends real OTP to phone."""
    print("\n" + "=" * 50)
    print("TEST: Auth Initiation")
    print("=" * 50)
    try:
        resp = requests.post(
            f"{BASE_URL}/auth",
            json={
                "mobileNumber": phone,
                "mobileCountryCode": "+91",
                "merchantId": "NAMMA_YATRI",
                "clientId": "NAMMA_YATRI"
            }
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        print(f"  Auth ID: {data.get('authId')}")
        print(f"  Attempts: {data.get('attempts')}")
        print(f"  OTP sent to: {phone}")
        return data.get("authId")
    except Exception as e:
        print(f"  ERROR: {e}")
        return None


def test_verify(auth_id, otp):
    """Test OTP verification."""
    print("\n" + "=" * 50)
    print("TEST: OTP Verification")
    print("=" * 50)
    try:
        resp = requests.post(
            f"{BASE_URL}/auth/{auth_id}/verify",
            json={
                "otp": otp,
                "deviceToken": "test_device_token"
            }
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        if resp.status_code == 200:
            print(f"  Token: {data.get('token')}")
            print(f"  User ID: {data.get('userId')}")
            return data.get("token")
        else:
            print(f"  Error: {data.get('errorCode')}")
            return None
    except Exception as e:
        print(f"  ERROR: {e}")
        return None


def test_stations(token):
    """Test metro stations endpoint."""
    print("\n" + "=" * 50)
    print("TEST: Metro Stations")
    print("=" * 50)
    try:
        resp = requests.get(
            f"{BASE_URL}/frfs/stations",
            params={"city": "chennai", "vehicleType": '"METRO"'},
            headers={"token": token}
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        print(f"  Total stations: {len(data)}")
        if len(data) > 0:
            print(f"  Sample: {data[0]['name']} ({data[0]['code']})")
        return data
    except Exception as e:
        print(f"  ERROR: {e}")
        return []


def test_routes(token):
    """Test metro routes endpoint."""
    print("\n" + "=" * 50)
    print("TEST: Metro Routes")
    print("=" * 50)
    try:
        resp = requests.get(
            f"{BASE_URL}/frfs/routes",
            params={"city": "chennai", "vehicleType": '"METRO"'},
            headers={"token": token}
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        print(f"  Total routes: {len(data)}")
        for route in data:
            print(f"  - {route['shortName']}: {route['longName']}")
        return data
    except Exception as e:
        print(f"  ERROR: {e}")
        return []


def test_search(token, from_code, to_code):
    """Test fare search."""
    print("\n" + "=" * 50)
    print("TEST: Fare Search")
    print("=" * 50)
    try:
        resp = requests.post(
            f"{BASE_URL}/frfs/search",
            params={"city": "chennai", "vehicleType": '"METRO"'},
            json={
                "fromStationCode": from_code,
                "toStationCode": to_code,
                "quantity": 1
            },
            headers={"token": token}
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        print(f"  Search ID: {data.get('searchId')}")
        return data.get("searchId")
    except Exception as e:
        print(f"  ERROR: {e}")
        return None


def test_quote(token, search_id):
    """Test fare quote."""
    print("\n" + "=" * 50)
    print("TEST: Fare Quote")
    print("=" * 50)
    try:
        time.sleep(2)
        resp = requests.get(
            f"{BASE_URL}/frfs/search/{search_id}/quote",
            params={"city": "chennai", "vehicleType": '"METRO"'},
            headers={"token": token}
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        for quote in data:
            print(f"  - {quote['_type']}: {quote['price']} {quote['priceWithCurrency']['currency']}")
            print(f"    Quote ID: {quote['quoteId']}")
        return data[0]["quoteId"] if data else None
    except Exception as e:
        print(f"  ERROR: {e}")
        return None


def test_booking(token, quote_id):
    """Test booking confirmation."""
    print("\n" + "=" * 50)
    print("TEST: Booking Confirmation")
    print("=" * 50)
    try:
        resp = requests.post(
            f"{BASE_URL}/frfs/quote/{quote_id}/confirm",
            params={"city": "chennai", "vehicleType": '"METRO"'},
            json={"quantity": 1},
            headers={"token": token}
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        print(f"  Booking ID: {data.get('bookingId')}")
        print(f"  Status: {data.get('status')}")
        print(f"  Price: {data.get('price')} {data.get('priceWithCurrency', {}).get('currency')}")
        return data.get("bookingId")
    except Exception as e:
        print(f"  ERROR: {e}")
        return None


def test_payment_status(token, booking_id):
    """Test payment status."""
    print("\n" + "=" * 50)
    print("TEST: Payment Status")
    print("=" * 50)
    try:
        resp = requests.get(
            f"{BASE_URL}/frfs/booking/{booking_id}/status",
            params={"city": "chennai", "vehicleType": '"METRO"'},
            headers={"token": token}
        )
        data = resp.json()
        print(f"  Status: {resp.status_code}")
        print(f"  Booking Status: {data.get('status')}")
        print(f"  Price: {data.get('price')}")
        
        payment = data.get("payment", {})
        if payment:
            order = payment.get("paymentOrder", {})
            print(f"  Juspay Order: {order.get('order_id')}")
            print(f"  Payment Link: {order.get('payment_links', {}).get('web')}")
        
        return data
    except Exception as e:
        print(f"  ERROR: {e}")
        return None


def main():
    print("MetroGo - API Test Suite")
    print("=" * 50)
    
    # Test 1: Health
    if not test_health():
        print("API is not reachable. Exiting.")
        sys.exit(1)
    
    # Test 2: Auth (requires real phone)
    phone = input("\nEnter phone number for OTP (or 'skip' to skip auth): ").strip()
    if phone.lower() == "skip":
        token = input("Enter existing token: ").strip()
    else:
        auth_id = test_auth(phone)
        if not auth_id:
            print("Auth failed. Exiting.")
            sys.exit(1)
        
        otp = input("Enter 4-digit OTP: ").strip()
        token = test_verify(auth_id, otp)
        if not token:
            print("Verification failed. Exiting.")
            sys.exit(1)
    
    print(f"\nUsing token: {token}")
    
    # Test 3: Stations
    stations = test_stations(token)
    
    # Test 4: Routes
    routes = test_routes(token)
    
    # Test 5: Search
    search_id = test_search(token, "SVA|0225", "SGM|0115")
    if not search_id:
        print("Search failed. Exiting.")
        sys.exit(1)
    
    # Test 6: Quote
    quote_id = test_quote(token, search_id)
    if not quote_id:
        print("Quote failed. Exiting.")
        sys.exit(1)
    
    # Test 7: Booking
    confirm = input("\nConfirm booking? (yes/no): ").strip().lower()
    if confirm == "yes":
        booking_id = test_booking(token, quote_id)
        if booking_id:
            test_payment_status(token, booking_id)
    
    print("\n" + "=" * 50)
    print("All tests completed.")
    print("=" * 50)


if __name__ == "__main__":
    main()
