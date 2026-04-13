import prologue
import ./handlers
import ./config

proc main() =
  var settings = newSettings(
    appName = APP_NAME,
    port = Port(SERVER_PORT),
    address = SERVER_HOST
  )

  let app = newApp(settings = settings)

  app.get("/", health)

  app.post("/auth", initAuth)
  app.post("/auth/{authId}/verify", verifyAuth)

  app.get("/metro/stations", getStations)
  app.get("/metro/routes", getRoutes)

  app.post("/metro/search", searchFare)
  app.get("/metro/search/{searchId}/quote", getQuote)

  app.post("/metro/quote/{quoteId}/confirm", confirmBooking)
  app.get("/booking/{bookingId}/status", getBookingStatus)
  app.get("/booking/{bookingId}/refresh", refreshBookingStatus)
  app.get("/debug", debugRoute)

  app.get("/profile", getProfile)
  app.get("/tickets", getTicketBookings)

  app.run()

when isMainModule:
  main()