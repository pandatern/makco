import std/[httpcore, json, strutils, asyncdispatch, httpclient, tables, options]


const
  BASE_URL = "https://api.moving.tech/pilot/app/v2"
  MERCHANT_ID = "NAMMA_YATRI"
  CLIENT_ID = "NAMMA_YATRI"
  VEHICLE_TYPE = "\"METRO\""


proc makeRequest(url: string, httpMethod: HttpMethod, token: string = "",
                 body: JsonNode = nil, params: seq[(string, string)] = @[]): Future[JsonNode] {.async.} =
  var client = newAsyncHttpClient()
  defer: client.close()

  var fullUrl = url
  if params.len > 0:
    var queryParts: seq[string]
    for (k, v) in params:
      queryParts.add(k & "=" & v)
    fullUrl = fullUrl & "?" & queryParts.join("&")

  if token.len > 0:
    client.headers = newHttpHeaders({
      "Content-Type": "application/json",
      "token": token
    })
  else:
    client.headers = newHttpHeaders({
      "Content-Type": "application/json"
    })

  try:
    var resp: AsyncResponse
    if httpMethod == HttpPost and body != nil:
      resp = await client.request(fullUrl, httpMethod = HttpPost, body = $body)
    elif httpMethod == HttpGet:
      resp = await client.request(fullUrl, httpMethod = HttpGet)
    else:
      resp = await client.request(fullUrl, httpMethod = httpMethod)

    let respBody = await resp.body
    try:
      result = parseJson(respBody)
    except:
      result = %*{"error": "Failed to parse response", "raw": respBody, "status": $resp.status}
  except CatchableError as e:
    result = %*{"error": "Request failed", "url": fullUrl, "msg": e.msg}


proc mtAuth*(phone, countryCode: string): Future[JsonNode] {.async.} =
  let body = %*{
    "mobileNumber": phone,
    "mobileCountryCode": countryCode,
    "merchantId": MERCHANT_ID,
    "clientId": CLIENT_ID
  }
  result = await makeRequest(BASE_URL & "/auth", HttpPost, body = body)


proc mtVerifyAuth*(authId, otp, deviceToken: string): Future[JsonNode] {.async.} =
  let body = %*{
    "otp": otp,
    "deviceToken": deviceToken
  }
  result = await makeRequest(BASE_URL & "/auth/" & authId & "/verify", HttpPost, body = body)


proc mtGetStations*(token, city: string): Future[JsonNode] {.async.} =
  result = await makeRequest(
    BASE_URL & "/frfs/stations",
    HttpGet,
    token = token,
    params = @[("city", city), ("vehicleType", VEHICLE_TYPE)]
  )


proc mtGetRoutes*(token, city: string): Future[JsonNode] {.async.} =
  result = await makeRequest(
    BASE_URL & "/frfs/routes",
    HttpGet,
    token = token,
    params = @[("city", city), ("vehicleType", VEHICLE_TYPE)]
  )


proc mtSearchFare*(token, city, fromCode, toCode: string, quantity: int): Future[JsonNode] {.async.} =
  let body = %*{
    "fromStationCode": fromCode,
    "toStationCode": toCode,
    "quantity": quantity
  }
  result = await makeRequest(
    BASE_URL & "/frfs/search",
    HttpPost,
    token = token,
    body = body,
    params = @[("city", city), ("vehicleType", VEHICLE_TYPE)]
  )


proc mtGetQuote*(token, searchId, city: string): Future[JsonNode] {.async.} =
  result = await makeRequest(
    BASE_URL & "/frfs/search/" & searchId & "/quote",
    HttpGet,
    token = token,
    params = @[("city", city), ("vehicleType", VEHICLE_TYPE)]
  )


proc mtConfirmBooking*(token, quoteId, city: string, quantity: int, mockPayment: bool = false): Future[JsonNode] {.async.} =
  let body = %*{"quantity": quantity}
  result = await makeRequest(
    BASE_URL & "/frfs/quote/" & quoteId & "/confirm",
    HttpPost,
    token = token,
    body = body,
    params = @[("city", city), ("vehicleType", VEHICLE_TYPE), ("isMockPayment", $mockPayment)]
  )


proc mtGetBookingStatus*(token, bookingId, city: string): Future[JsonNode] {.async.} =
  result = await makeRequest(
    BASE_URL & "/frfs/booking/" & bookingId & "/status",
    HttpGet,
    token = token,
    params = @[("city", city), ("vehicleType", VEHICLE_TYPE)]
  )


proc mtGetProfile*(token: string): Future[JsonNode] {.async.} =
  result = await makeRequest(
    BASE_URL & "/profile",
    HttpGet,
    token = token
  )


proc mtGetTicketBookings*(token, city: string): Future[JsonNode] {.async.} =
  result = await makeRequest(
    BASE_URL & "/ticket/bookings/v2",
    HttpGet,
    token = token,
    params = @[("city", city), ("vehicleType", VEHICLE_TYPE)]
  )
