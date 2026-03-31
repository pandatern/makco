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

  # Health
  app.get("/", health)

  # Auth
  app.post("/auth", initAuth)
  app.post("/auth/{authId}/verify", verifyAuth)

  # Metro
  app.get("/metro/stations", getStations)
  app.get("/metro/routes", getRoutes)

  # Search
  app.post("/metro/search", searchFare)
  app.get("/metro/search/{searchId}/quote", getQuote)

  # Booking
  app.post("/metro/quote/{quoteId}/confirm", confirmBooking)
  app.get("/metro/booking/{bookingId}/status", getBookingStatus)

  # User
  app.get("/profile", getProfile)
  app.get("/tickets", getTicketBookings)

  app.run()

when isMainModule:
  main()
