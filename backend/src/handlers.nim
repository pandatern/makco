import json, strutils, asyncdispatch
import prologue
import ./api_client


proc health*(ctx: Context) {.async.} =
  resp jsonResponse(%*{
    "status": "ok",
    "app": "Makco",
    "version": "1.0.0"
  })


proc initAuth*(ctx: Context) {.async.} =
  let body = ctx.request.body.parseJson
  let phone = body{"mobileNumber"}.getStr
  let countryCode = body{"mobileCountryCode"}.getStr("+91")

  if phone.len != 10 or not phone.allCharsInSet({'0'..'9'}):
    resp jsonResponse(%*{"error": "Invalid phone number"}, Http400)
    return

  let result = await mtAuth(phone, countryCode)
  resp jsonResponse(result)


proc verifyAuth*(ctx: Context) {.async.} =
  let authId = ctx.getPathParams("authId", "")
  let body = ctx.request.body.parseJson
  let otp = body{"otp"}.getStr
  let deviceToken = body{"deviceToken"}.getStr("makco_device")

  if otp.len != 4:
    resp jsonResponse(%*{"error": "OTP must be 4 digits"}, Http400)
    return

  let result = await mtVerifyAuth(authId, otp, deviceToken)
  resp jsonResponse(result)


proc getStations*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let city = ctx.getQueryParams("city", "chennai")

  let result = await mtGetStations(token, city)
  resp jsonResponse(result)


proc getRoutes*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let city = ctx.getQueryParams("city", "chennai")

  let result = await mtGetRoutes(token, city)
  resp jsonResponse(result)


proc searchFare*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let city = ctx.getQueryParams("city", "chennai")
  let body = ctx.request.body.parseJson

  let fromStation = body{"fromStationCode"}.getStr
  let toStation = body{"toStationCode"}.getStr
  let quantity = body{"quantity"}.getInt(1)

  let result = await mtSearchFare(token, city, fromStation, toStation, quantity)
  resp jsonResponse(result)


proc getQuote*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let searchId = ctx.getPathParams("searchId", "")
  let city = ctx.getQueryParams("city", "chennai")

  let result = await mtGetQuote(token, searchId, city)
  resp jsonResponse(result)


proc confirmBooking*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let quoteId = ctx.getPathParams("quoteId", "")
  let city = ctx.getQueryParams("city", "chennai")
  let body = ctx.request.body.parseJson
  let quantity = body{"quantity"}.getInt(1)

  let result = await mtConfirmBooking(token, quoteId, city, quantity)
  resp jsonResponse(result)


proc getBookingStatus*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let bookingId = ctx.getPathParams("bookingId", "")
  let city = ctx.getQueryParams("city", "chennai")

  let result = await mtGetBookingStatus(token, bookingId, city)
  resp jsonResponse(result)


proc getProfile*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let result = await mtGetProfile(token)
  resp jsonResponse(result)


proc getTicketBookings*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  let city = ctx.getQueryParams("city", "chennai")

  let result = await mtGetTicketBookings(token, city)
  resp jsonResponse(result)
