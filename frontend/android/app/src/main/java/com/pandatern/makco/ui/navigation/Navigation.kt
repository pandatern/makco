package com.pandatern.makco.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.pandatern.makco.data.local.TokenManager
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.screens.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object Auth : Screen()
    object Home : Screen()
    object SourcePicker : Screen()
    object DestinationPicker : Screen()
    object Booking : Screen()
    object Payment : Screen()
    object PaymentWeb : Screen()
    object Ticket : Screen()
    object TicketHistory : Screen()
    object Profile : Screen()
}

@Composable
fun MakcoNavHost() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var token by remember { mutableStateOf("") }

    var stations by remember { mutableStateOf<List<Station>>(emptyList()) }
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }

    var selectedSource by remember { mutableStateOf<Station?>(null) }
    var selectedDestination by remember { mutableStateOf<Station?>(null) }

    var quotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }
    var selectedQuantity by remember { mutableStateOf(1) }
    var searchId by remember { mutableStateOf<String?>(null) }

    var bookingId by remember { mutableStateOf<String?>(null) }
    var bookingStatus by remember { mutableStateOf<BookingStatus?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun loadMetroData() {
        scope.launch {
            try {
                val s = ApiClient.instance.getStations(token)
                if (s.isSuccessful) stations = s.body() ?: emptyList()
                val r = ApiClient.instance.getRoutes(token)
                if (r.isSuccessful) routes = r.body() ?: emptyList()
            } catch (_: Exception) {}
        }
    }

    fun startSearch() {
        if (selectedSource == null || selectedDestination == null) return
        isLoading = true
        error = null
        quotes = emptyList()
        scope.launch {
            try {
                val resp = ApiClient.instance.searchFare(
                    token = token,
                    request = SearchRequest(
                        fromStationCode = selectedSource!!.code,
                        toStationCode = selectedDestination!!.code
                    )
                )
                if (resp.isSuccessful && resp.body() != null) {
                    searchId = resp.body()!!.searchId
                    delay(2500)
                    val quoteResp = ApiClient.instance.getQuote(token, searchId!!)
                    if (quoteResp.isSuccessful) {
                        quotes = quoteResp.body() ?: emptyList()
                    }
                } else {
                    error = "Search failed"
                }
            } catch (e: Exception) {
                error = e.message
            }
            isLoading = false
        }
    }

    fun confirmBooking(quote: Quote, quantity: Int) {
        isLoading = true
        error = null
        selectedQuote = quote
        selectedQuantity = quantity
        scope.launch {
            try {
                val resp = ApiClient.instance.confirmBooking(
                    token = token,
                    quoteId = quote.quoteId
                )
                if (resp.isSuccessful && resp.body() != null) {
                    bookingId = resp.body()!!.bookingId
                    currentScreen = Screen.Payment
                    // Fetch payment details
                    delay(1000)
                    val statusResp = ApiClient.instance.getBookingStatus(token, bookingId!!)
                    if (statusResp.isSuccessful) {
                        bookingStatus = statusResp.body()
                    }
                } else {
                    error = "Booking failed"
                }
            } catch (e: Exception) {
                error = e.message
            }
            isLoading = false
        }
    }

    // Determine start screen from saved state
    LaunchedEffect(Unit) {
        val savedToken = TokenManager.getToken(context)
        val onboardingDone = TokenManager.isOnboardingDone(context)

        if (savedToken != null && savedToken.isNotEmpty()) {
            token = savedToken
            loadMetroData()
            currentScreen = Screen.Home
        } else if (onboardingDone) {
            currentScreen = Screen.Auth
        }
    }

    when (currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                onFinished = {
                    val onboardingDone = TokenManager.isOnboardingDone(context)
                    val savedToken = TokenManager.getToken(context)

                    if (savedToken != null && savedToken.isNotEmpty()) {
                        token = savedToken
                        loadMetroData()
                        currentScreen = Screen.Home
                    } else if (onboardingDone) {
                        currentScreen = Screen.Auth
                    } else {
                        currentScreen = Screen.Onboarding
                    }
                }
            )
        }
        is Screen.Onboarding -> {
            OnboardingScreen(
                onFinished = {
                    TokenManager.setOnboardingDone(context)
                    currentScreen = Screen.Auth
                }
            )
        }
        is Screen.Auth -> {
            AuthScreen(
                onAuthSuccess = { newToken ->
                    token = newToken
                    TokenManager.saveToken(context, newToken)
                    loadMetroData()
                    currentScreen = Screen.Home
                }
            )
        }
        is Screen.Home -> {
            HomeScreen(
                stations = stations,
                selectedSource = selectedSource,
                selectedDestination = selectedDestination,
                onStationClick = { isSource ->
                    currentScreen = if (isSource) Screen.SourcePicker else Screen.DestinationPicker
                },
                onSearchClick = {
                    currentScreen = Screen.Booking
                    startSearch()
                },
                onProfileClick = {
                    currentScreen = Screen.Profile
                },
                onTicketsClick = {
                    currentScreen = Screen.TicketHistory
                }
            )
        }
        is Screen.SourcePicker -> {
            StationPickerScreen(
                stations = stations,
                onStationSelected = { station ->
                    selectedSource = station
                    currentScreen = Screen.Home
                },
                onBack = { currentScreen = Screen.Home }
            )
        }
        is Screen.DestinationPicker -> {
            StationPickerScreen(
                stations = stations,
                onStationSelected = { station ->
                    selectedDestination = station
                    currentScreen = Screen.Home
                },
                onBack = { currentScreen = Screen.Home }
            )
        }
        is Screen.Booking -> {
            BookingScreen(
                quotes = quotes,
                fromStation = selectedSource,
                toStation = selectedDestination,
                isLoading = isLoading,
                error = error,
                onConfirm = { quote, quantity ->
                    confirmBooking(quote, quantity)
                },
                onRetry = { startSearch() },
                onBack = {
                    currentScreen = Screen.Home
                    quotes = emptyList()
                    error = null
                }
            )
        }
        is Screen.Payment -> {
            PaymentScreen(
                bookingStatus = bookingStatus,
                isLoading = isLoading,
                error = error,
                onPayClick = {
                    currentScreen = Screen.PaymentWeb
                },
                onViewTicket = {
                    currentScreen = Screen.Ticket
                },
                onBack = {
                    currentScreen = Screen.Home
                    bookingId = null
                    bookingStatus = null
                    selectedSource = null
                    selectedDestination = null
                    quotes = emptyList()
                }
            )
        }
        is Screen.PaymentWeb -> {
            val payUrl = bookingStatus?.payment?.order?.paymentLinks?.web ?: ""
            PaymentWebView(
                paymentUrl = payUrl,
                onPaymentComplete = {
                    currentScreen = Screen.Ticket
                },
                onBack = { currentScreen = Screen.Payment }
            )
        }
        is Screen.Ticket -> {
            TicketScreen(
                token = token,
                bookingId = bookingId ?: "",
                onBack = {
                    currentScreen = Screen.Home
                    bookingId = null
                    bookingStatus = null
                    selectedSource = null
                    selectedDestination = null
                    quotes = emptyList()
                }
            )
        }
        is Screen.TicketHistory -> {
            TicketHistoryScreen(
                token = token,
                onTicketClick = { id ->
                    bookingId = id
                    currentScreen = Screen.Ticket
                },
                onBack = { currentScreen = Screen.Home }
            )
        }
        is Screen.Profile -> {
            ProfileScreen(
                token = token,
                onBack = { currentScreen = Screen.Home },
                onLogout = {
                    token = ""
                    TokenManager.clearToken(context)
                    currentScreen = Screen.Auth
                }
            )
        }
    }
}
