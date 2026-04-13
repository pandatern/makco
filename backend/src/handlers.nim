import json, strutils, asyncdispatch
import prologue
import ./api_client

proc jsonResponse(ctx: Context, data: JsonNode, code: HttpCode = Http200) =
  ctx.response.code = code
  ctx.response.body = $(data)

proc health*(ctx: Context) {.async.} =
  ctx.jsonResponse(%*{
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
      ctx.jsonResponse(%*{"error": "Invalid phone number"}, Http400)
      return

    let result = await mtAuth(phone, countryCode)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Auth failed", "message": e.msg}, Http500)

proc verifyAuth*(ctx: Context) {.async.} =
  try:
    let authId = ctx.getPathParams("authId", "")
    let body = ctx.request.body.parseJson
    let otp = body{"otp"}.getStr
    let deviceToken = body{"deviceToken"}.getStr("makco_device")

    if authId.len == 0:
      ctx.jsonResponse(%*{"error": "Missing authId"}, Http400)
      return

    if otp.len != 4:
      ctx.jsonResponse(%*{"error": "OTP must be 4 digits"}, Http400)
      return

    let result = await mtVerifyAuth(authId, otp, deviceToken)

    if result.hasKey("errorCode"):
      ctx.jsonResponse(result, Http400)
    else:
      ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Verify failed", "message": e.msg}, Http500)

proc getStations*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetStations(token, city)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Failed to get stations", "message": e.msg}, Http500)

proc getRoutes*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetRoutes(token, city)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Failed to get routes", "message": e.msg}, Http500)

proc searchFare*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")
    let body = ctx.request.body.parseJson

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let fromStation = body{"fromStationCode"}.getStr
    let toStation = body{"toStationCode"}.getStr
    let quantity = body{"quantity"}.getInt(1)

    if fromStation.len == 0 or toStation.len == 0:
      ctx.jsonResponse(%*{"error": "Missing station codes"}, Http400)
      return

    let result = await mtSearchFare(token, city, fromStation, toStation, quantity)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Search failed", "message": e.msg}, Http500)

proc getQuote*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let searchId = ctx.getPathParams("searchId", "")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    if searchId.len == 0:
      ctx.jsonResponse(%*{"error": "Missing searchId"}, Http400)
      return

    let result = await mtGetQuote(token, searchId, city)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Quote failed", "message": e.msg}, Http500)

proc confirmBooking*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let quoteId = ctx.getPathParams("quoteId", "")
    let city = ctx.getQueryParams("city", "chennai")
    let body = ctx.request.body.parseJson
    let quantity = body{"quantity"}.getInt(1)

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    if quoteId.len == 0:
      ctx.jsonResponse(%*{"error": "Missing quoteId"}, Http400)
      return

    # Admin skip payment
    let isAdmin = token == "admin_token_6374746721" or token.startsWith("admin_")
    var mockPayment = false
    if isAdmin:
      mockPayment = true
      echo "[ADMIN] Skipping payment for quote: ", quoteId
    
    let result = await mtConfirmBooking(token, quoteId, city, quantity, mockPayment)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Confirm failed", "message": e.msg}, Http500)

proc getBookingStatus*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let bookingId = ctx.getPathParams("bookingId", "")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    if bookingId.len == 0:
      ctx.jsonResponse(%*{"error": "Missing bookingId"}, Http400)
      return

    let result = await mtGetBookingStatus(token, bookingId, city)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Status failed", "message": e.msg}, Http500)

proc refreshBookingStatus*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let bookingId = ctx.getPathParams("bookingId", "")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    if bookingId.len == 0:
      ctx.jsonResponse(%*{"error": "Missing bookingId"}, Http400)
      return

    let result = await mtGetBookingStatus(token, bookingId, city)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Refresh failed", "message": e.msg}, Http500)

proc debugRoute*(ctx: Context) {.async.} =
  let token = ctx.request.headers.getOrDefault("token")
  
  if token != "admin_token_6374746721" and not token.startsWith("admin_"):
    ctx.jsonResponse(%*{"error": "Unauthorized"}, Http401)
    return
  
  var debugData = newJObject()
  debugData["app"] = "Makco"
  debugData["version"] = "1.0.0"
  debugData["admin"] = true
  debugData["features"] = %*{
    "skip_payment": true,
    "debug_logs": true
  }
  
  ctx.jsonResponse(debugData)

proc getProfile*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetProfile(token)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Profile failed", "message": e.msg}, Http500)

proc getTicketBookings*(ctx: Context) {.async.} =
  try:
    let token = ctx.request.headers.getOrDefault("token")
    let city = ctx.getQueryParams("city", "chennai")

    if token.len == 0:
      ctx.jsonResponse(%*{"errorCode": "MISSING_HEADER", "errorMessage": "Header token is missing"}, Http401)
      return

    let result = await mtGetTicketBookings(token, city)
    ctx.jsonResponse(result)
  except CatchableError as e:
    ctx.jsonResponse(%*{"error": "Tickets failed", "message": e.msg}, Http500)
