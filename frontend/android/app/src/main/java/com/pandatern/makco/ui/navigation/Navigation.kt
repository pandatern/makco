package com.pandatern.makco.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pandatern.makco.data.local.TokenManager
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.screens.*
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object Auth : Screen()
    object Main : Screen()
    object SourcePicker : Screen()
    object DestinationPicker : Screen()
    object Booking : Screen()
    object Payment : Screen()
    object PaymentWeb : Screen()
    object Ticket : Screen()
}

@Composable
fun MakcoNavHost() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var token by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

    var stations by remember { mutableStateOf<List<Station>>(emptyList()) }
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
                    if (quoteResp.isSuccessful) quotes = quoteResp.body() ?: emptyList()
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
                    delay(1000)
                    val statusResp = ApiClient.instance.getBookingStatus(token, bookingId!!)
                    if (statusResp.isSuccessful) bookingStatus = statusResp.body()
                } else {
                    error = "Booking failed"
                }
            } catch (e: Exception) {
                error = e.message
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        val savedToken = TokenManager.getToken(context)
        val onboardingDone = TokenManager.isOnboardingDone(context)
        if (savedToken != null && savedToken.isNotEmpty()) {
            token = savedToken
            loadMetroData()
            currentScreen = Screen.Main
        } else if (onboardingDone) {
            currentScreen = Screen.Auth
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            is Screen.Splash -> {
                SplashScreen {
                    val savedToken = TokenManager.getToken(context)
                    if (savedToken != null && savedToken.isNotEmpty()) {
                        token = savedToken
                        loadMetroData()
                        currentScreen = Screen.Main
                    } else if (TokenManager.isOnboardingDone(context)) {
                        currentScreen = Screen.Auth
                    } else {
                        currentScreen = Screen.Onboarding
                    }
                }
            }
            is Screen.Onboarding -> {
                OnboardingScreen {
                    TokenManager.setOnboardingDone(context)
                    currentScreen = Screen.Auth
                }
            }
            is Screen.Auth -> {
                AuthScreen { newToken ->
                    token = newToken
                    TokenManager.saveToken(context, newToken)
                    loadMetroData()
                    currentScreen = Screen.Main
                }
            }
            is Screen.Main -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            BottomTab.HOME -> HomeScreen(
                                stations = stations,
                                selectedSource = selectedSource,
                                selectedDestination = selectedDestination,
                                onStationClick = { isSource ->
                                    currentScreen = if (isSource) Screen.SourcePicker else Screen.DestinationPicker
                                },
                                onSearchClick = {
                                    currentScreen = Screen.Booking
                                    startSearch()
                                }
                            )
                            BottomTab.TICKETS -> TicketHistoryScreen(
                                token = token,
                                onTicketClick = { id ->
                                    bookingId = id
                                    currentScreen = Screen.Ticket
                                }
                            )
                            BottomTab.PROFILE -> ProfileScreen(
                                token = token,
                                onLogout = {
                                    token = ""
                                    TokenManager.clearToken(context)
                                    currentScreen = Screen.Auth
                                }
                            )
                        }
                    }
                    BottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
                }
            }
            is Screen.SourcePicker -> {
                StationPickerScreen(
                    stations = stations,
                    onStationSelected = { station ->
                        selectedSource = station
                        currentScreen = Screen.Main
                    },
                    onBack = { currentScreen = Screen.Main }
                )
            }
            is Screen.DestinationPicker -> {
                StationPickerScreen(
                    stations = stations,
                    onStationSelected = { station ->
                        selectedDestination = station
                        currentScreen = Screen.Main
                    },
                    onBack = { currentScreen = Screen.Main }
                )
            }
            is Screen.Booking -> {
                BookingScreen(
                    quotes = quotes,
                    fromStation = selectedSource,
                    toStation = selectedDestination,
                    isLoading = isLoading,
                    error = error,
                    onConfirm = { quote, quantity -> confirmBooking(quote, quantity) },
                    onRetry = { startSearch() },
                    onBack = {
                        currentScreen = Screen.Main
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
                    onPayClick = { currentScreen = Screen.PaymentWeb },
                    onViewTicket = { currentScreen = Screen.Ticket },
                    onBack = {
                        currentScreen = Screen.Main
                        bookingId = null
                        bookingStatus = null
                    }
                )
            }
            is Screen.PaymentWeb -> {
                val payUrl = bookingStatus?.payment?.order?.paymentLinks?.web ?: ""
                PaymentWebView(
                    paymentUrl = payUrl,
                    onPaymentComplete = { currentScreen = Screen.Ticket },
                    onBack = { currentScreen = Screen.Payment }
                )
            }
            is Screen.Ticket -> {
                TicketScreen(
                    token = token,
                    bookingId = bookingId ?: "",
                    onBack = {
                        currentScreen = Screen.Main
                        bookingId = null
                        bookingStatus = null
                    }
                )
            }
        }
    }
}
