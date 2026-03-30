import prologue
import ./handlers


proc main() =
  let app = newApp()

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
