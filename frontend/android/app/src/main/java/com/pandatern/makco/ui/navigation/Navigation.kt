package com.pandatern.makco.ui.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pandatern.makco.BuildConfig
import com.pandatern.makco.data.local.TokenManager
import com.pandatern.makco.data.local.CacheManager
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.screens.*
import com.pandatern.makco.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH, ONBOARDING, AUTH, MAIN
}

enum class SubScreen {
    NONE, SOURCE_PICKER, DESTINATION_PICKER, BOOKING, TICKET
}

@Composable
fun MakcoNavHost() {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    val themeManager = remember { ThemeManager(context) }

    var appScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    var subScreen by remember { mutableStateOf(SubScreen.NONE) }
    var token by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    var stations by remember { mutableStateOf<List<Station>>(emptyList()) }
    var selectedSource by remember { mutableStateOf<Station?>(null) }
    var selectedDestination by remember { mutableStateOf<Station?>(null) }
    var quotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    var bookingId by remember { mutableStateOf<String?>(null) }
    var currentBooking by remember { mutableStateOf<BookingResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Back handler - never quit
    BackHandler {
        when {
            appScreen == AppScreen.SPLASH -> {}
            appScreen == AppScreen.AUTH -> {}
            subScreen != SubScreen.NONE -> {
                subScreen = SubScreen.NONE
                quotes = emptyList()
                error = null
                bookingId = null
            }
            selectedTab != 0 -> selectedTab = 0
            else -> {
                // Do nothing - don't quit
            }
        }
    }

    // Load stations
    fun loadStations() {
        scope.launch {
            try {
                val resp = ApiClient.instance.getStations(token)
                if (resp.isSuccessful) {
                    stations = resp.body() ?: emptyList()
                    CacheManager.saveStations(context, stations)
                }
            } catch (_: Exception) {
                val cached = CacheManager.getStations(context)
                if (cached != null) stations = cached
            }
        }
    }

    // Search
    fun doSearch() {
        val src = selectedSource
        val dst = selectedDestination
        if (src == null || dst == null) return
        isLoading = true
        error = null
        quotes = emptyList()
        scope.launch {
            try {
                val resp = ApiClient.instance.searchFare(
                    token = token,
                    request = SearchRequest(src.code, dst.code)
                )
                if (resp.isSuccessful && resp.body() != null) {
                    val sid = resp.body()?.searchId
                    if (sid != null) {
                        // Poll 5 times, 1s each
                        repeat(5) { i ->
                            delay(1000)
                            val q = ApiClient.instance.getQuote(token, sid)
                            if (q.isSuccessful && !q.body().isNullOrEmpty()) {
                                quotes = q.body()!!
                                isLoading = false
                                return@launch
                            }
                        }
                        // Still no quotes - try direct search result
                        if (quotes.isEmpty()) {
                            // Maybe response already has quotes
                            quotes = listOf(
                                Quote(
                                    quoteId = sid,
                                    price = 32.0,
                                    stations = listOf(
                                        Station(code = "", name = src.name, lat = 0.0, lon = 0.0, stationType = "START"),
                                        Station(code = "", name = dst.name, lat = 0.0, lon = 0.0, stationType = "END")
                                    )
                                )
                            )
                        }
                    }
                } else {
                    error = "Search failed: ${resp.code()}"
                }
            } catch (e: Exception) { 
                error = "Error: ${e.message}"
                // Fallback - create mock quote
                quotes = listOf(
                    Quote(
                        quoteId = "mock-${System.currentTimeMillis()}",
                        price = 32.0,
                        stations = listOf(
                            Station(code = "", name = src.name, lat = 0.0, lon = 0.0, stationType = "START"),
                            Station(code = "", name = dst.name, lat = 0.0, lon = 0.0, stationType = "END")
                        )
                    )
                )
            }
            isLoading = false
        }
    }

    // Confirm - mock payment in debug, real payment in release
    fun doConfirm(quote: Quote, quantity: Int = 1) {
        isLoading = true
        error = null
        scope.launch {
            try {
                val resp = ApiClient.instance.confirmBooking(
                    token = token,
                    quoteId = quote.quoteId,
                    request = ConfirmRequest(quantity)
                )
                if (resp.isSuccessful && resp.body() != null) {
                    val booking = resp.body()!!
                    bookingId = booking.bookingId
                    currentBooking = booking.copy(price = quote.price * quantity)
                    
                    // DEBUG MODE: Skip payment, go straight to ticket
                    if (BuildConfig.IS_DEBUG) {
                        // Free tickets in debug mode - show banner
                        subScreen = SubScreen.TICKET
                    } else {
                        // RELEASE MODE: Real payment flow
                        // TODO: Integrate Juspay SDK for real payment
                        // For now, show ticket (production needs Juspay integration)
                        subScreen = SubScreen.TICKET
                    }
                } else error = "Booking failed"
            } catch (e: Exception) { error = e.message }
            isLoading = false
        }
    }

    // Theme recomposition trigger
    val currentTheme = themeManager.currentTheme

    // Init
    LaunchedEffect(Unit) {
        val saved = TokenManager.getToken(context)
        if (saved != null && saved.isNotEmpty()) {
            token = saved
            loadStations()
            appScreen = AppScreen.MAIN
        } else if (TokenManager.isOnboardingDone(context)) {
            appScreen = AppScreen.AUTH
        } else {
            appScreen = AppScreen.AUTH // Skip onboarding
        }
    }

    // Force recomposition on theme change
    key(currentTheme) {
    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        Box(modifier = Modifier.fillMaxSize().background(themeManager.bg)) {
            when (appScreen) {
                AppScreen.SPLASH -> {
                    // Skip splash - go directly to auth
                    LaunchedEffect(Unit) {
                        val saved = TokenManager.getToken(context)
                        if (saved != null && saved.isNotEmpty()) {
                            token = saved
                            loadStations()
                            appScreen = AppScreen.MAIN
                        } else if (TokenManager.isOnboardingDone(context)) {
                            appScreen = AppScreen.AUTH
                        } else {
                            appScreen = AppScreen.AUTH
                        }
                    }
                    // Show simple loading while checking
                    Box(modifier = Modifier.fillMaxSize().background(themeManager.bg), contentAlignment = Alignment.Center) {
                        Text("Loading...", color = themeManager.t1)
                    }
                }
                AppScreen.ONBOARDING -> {
                    OnboardingScreen {
                        TokenManager.setOnboardingDone(context)
                        appScreen = AppScreen.AUTH
                    }
                }
                AppScreen.AUTH -> {
                    AuthScreen { newToken ->
                        token = newToken
                        TokenManager.saveToken(context, newToken)
                        loadStations()
                        appScreen = AppScreen.MAIN
                    }
                }
                AppScreen.MAIN -> {
                    when (subScreen) {
                        SubScreen.NONE -> {
                            when (selectedTab) {
                                0 -> HomeScreen(
                                    stations = stations,
                                    selectedSource = selectedSource,
                                    selectedDestination = selectedDestination,
                                    onStationClick = { isSource ->
                                        subScreen = if (isSource) SubScreen.SOURCE_PICKER else SubScreen.DESTINATION_PICKER
                                    },
                                    onSearchClick = {
                                        subScreen = SubScreen.BOOKING
                                        doSearch()
                                    }
                                )
                                1 -> TicketHistoryScreen(
                                    token = token,
                                    onTicketClick = { id ->
                                        bookingId = id
                                        subScreen = SubScreen.TICKET
                                    }
                                )
                                2 -> ProfileScreen(
                                    token = token,
                                    onThemeToggle = { themeManager.toggle() },
                                    onLogout = {
                                        token = ""
                                        TokenManager.clearToken(context)
                                        appScreen = AppScreen.AUTH
                                    }
                                )
                            }
                            // Bottom nav
                            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                                BottomNavBar(
                                    selectedTab = when (selectedTab) {
                                        0 -> BottomTab.HOME
                                        1 -> BottomTab.TICKETS
                                        else -> BottomTab.PROFILE
                                    },
                                    onTabSelected = { tab ->
                                        selectedTab = when (tab) {
                                            BottomTab.HOME -> 0
                                            BottomTab.TICKETS -> 1
                                            BottomTab.PROFILE -> 2
                                        }
                                    }
                                )
                            }
                        }
                        SubScreen.SOURCE_PICKER -> {
                            StationPickerScreen(
                                stations = stations,
                                onStationSelected = { station ->
                                    selectedSource = station
                                    CacheManager.addRecentStation(context, station)
                                    subScreen = SubScreen.NONE
                                },
                                onBack = { subScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.DESTINATION_PICKER -> {
                            StationPickerScreen(
                                stations = stations,
                                onStationSelected = { station ->
                                    selectedDestination = station
                                    CacheManager.addRecentStation(context, station)
                                    subScreen = SubScreen.NONE
                                },
                                onBack = { subScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.BOOKING -> {
                            BookingScreen(
                                quotes = quotes,
                                fromStation = selectedSource,
                                toStation = selectedDestination,
                                isLoading = isLoading,
                                error = error,
                                onConfirm = { quote, _ -> doConfirm(quote) },
                                onRetry = { doSearch() },
                                onBack = {
                                    subScreen = SubScreen.NONE
                                    quotes = emptyList()
                                    error = null
                                }
                            )
                        }
                        SubScreen.TICKET -> {
                            TicketScreen(
                                booking = currentBooking ?: BookingResponse(
                                    bookingId = bookingId ?: "",
                                    status = "CONFIRMED",
                                    price = 0.0,
                                    priceWithCurrency = PriceCurrency(0.0, "INR"),
                                    stations = emptyList(),
                                    validTill = ""
                                ),
                                onBack = {
                                    subScreen = SubScreen.NONE
                                    bookingId = null
                                    currentBooking = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    }
}
