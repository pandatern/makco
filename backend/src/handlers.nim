import json, strutils, asyncdispatch
import prologue
import ./api_client

proc jsonResponse(data: JsonNode, code: HttpCode = Http200): Response =
  resp code, $(data), "application/json"

proc health*(ctx: Context) {.async.} =
  jsonResponse(%*{
    "status": "ok",
    "app": "Makco",
    "version": "1.0.0"
  })

proc initAuth*(ctx: Context) {.async.} =
  try:
    let body = ctx.request.body.parseJson
    let phone = body{"mobileNumber"}.getStr
    let countryCode = body{"mobileCountryCode"}.getStr("+91")

    if phone.len != 10 or not phone.allCharsInSet({'0'..'9'}):
      jsonResponse(%*{"error": "Invalid phone number"}, Http400)
      return

    let result = await mtAuth(phone, countryCode)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Auth failed", "message": e.msg}, Http500)

proc verifyAuth*(ctx: Context) {.async.} =
  try:
    let authId = ctx.getPathParams("authId", "")
    let body = ctx.request.body.parseJson
    let otp = body{"otp"}.getStr
    let deviceToken = body{"deviceToken"}.getStr("makco_device")

    if authId.len == 0:
      jsonResponse(%*{"error": "Missing authId"}, Http400)
      return

    if otp.len != 4:
      jsonResponse(%*{"error": "OTP must be 4 digits"}, Http400)
      return

    let result = await mtVerifyAuth(authId, otp, deviceToken)

    if result.hasKey("errorCode"):
      jsonResponse(result, Http400)
    else:
      jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Verify failed", "message": e.msg}, Http500)

proc getStations*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetStations(token, city)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Failed to get stations", "message": e.msg}, Http500)

proc getRoutes*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetRoutes(token, city)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Failed to get routes", "message": e.msg}, Http500)

proc searchFare*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")
    let body = ctx.request.body.parseJson

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let fromStation = body{"fromStationCode"}.getStr
    let toStation = body{"toStationCode"}.getStr
    let quantity = body{"quantity"}.getInt(1)

    if fromStation.len == 0 or toStation.len == 0:
      jsonResponse(%*{"error": "Missing station codes"}, Http400)
      return

    let result = await mtSearchFare(token, city, fromStation, toStation, quantity)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Search failed", "message": e.msg}, Http500)

proc getQuote*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let searchId = ctx.getPathParams("searchId", "")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    if searchId.len == 0:
      jsonResponse(%*{"error": "Missing searchId"}, Http400)
      return

    let result = await mtGetQuote(token, searchId, city)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Quote failed", "message": e.msg}, Http500)

proc confirmBooking*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let quoteId = ctx.getPathParams("quoteId", "")
    let city = ctx.getQueryParams("city", "chennai")
    let body = ctx.request.body.parseJson
    let quantity = body{"quantity"}.getInt(1)

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    if quoteId.len == 0:
      jsonResponse(%*{"error": "Missing quoteId"}, Http400)
      return

    let result = await mtConfirmBooking(token, quoteId, city, quantity)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Confirm failed", "message": e.msg}, Http500)

proc getBookingStatus*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let bookingId = ctx.getPathParams("bookingId", "")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    if bookingId.len == 0:
      jsonResponse(%*{"error": "Missing bookingId"}, Http400)
      return

    let result = await mtGetBookingStatus(token, bookingId, city)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Status failed", "message": e.msg}, Http500)

proc getProfile*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetProfile(token)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Profile failed", "message": e.msg}, Http500)

proc getTicketBookings*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetTicketBookings(token, city)
    jsonResponse(result)
  except CatchableError as e:
    jsonResponse(%*{"error": "Tickets failed", "message": e.msg}, Http500)
