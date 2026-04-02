package com.pandatern.makco.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pandatern.makco.data.local.TokenManager
import com.pandatern.makco.data.local.CacheManager
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
    object Ticket : Screen()
}

@Composable
fun MakcoNavHost() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val themeManager = remember { ThemeManager(context) }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var token by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

    var stations by remember { mutableStateOf<List<Station>>(emptyList()) }
    var selectedSource by remember { mutableStateOf<Station?>(null) }
    var selectedDestination by remember { mutableStateOf<Station?>(null) }

    var quotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    var searchId by remember { mutableStateOf<String?>(null) }

    var bookingId by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Back handler
    BackHandler(enabled = currentScreen !is Screen.Splash && currentScreen !is Screen.Auth) {
        when (currentScreen) {
            is Screen.Main -> {
                if (selectedTab != BottomTab.HOME) selectedTab = BottomTab.HOME
            }
            else -> {
                currentScreen = Screen.Main
                bookingId = null
                quotes = emptyList()
                error = null
            }
        }
    }

    fun loadMetroData() {
        scope.launch {
            try {
                val s = ApiClient.instance.getStations(token)
                if (s.isSuccessful) {
                    stations = s.body() ?: emptyList()
                    CacheManager.saveStations(context, stations)
                }
            } catch (_: Exception) {
                val cached = CacheManager.getStations(context)
                if (cached != null) stations = cached
            }
        }
    }

    fun startSearch() {
        val src = selectedSource
        val dst = selectedDestination
        if (src == null || dst == null) return
        isLoading = true
        error = null
        quotes = emptyList()
        scope.launch {
            try {
                val resp = ApiClient.instance.searchFare(token = token, request = SearchRequest(src.code, dst.code))
                if (resp.isSuccessful && resp.body() != null) {
                    searchId = resp.body()?.searchId
                    if (searchId != null) {
                        var attempts = 0
                        while (attempts < 5) {
                            delay(1500)
                            val q = ApiClient.instance.getQuote(token, searchId!!)
                            if (q.isSuccessful && !q.body().isNullOrEmpty()) {
                                quotes = q.body()!!
                                break
                            }
                            attempts++
                        }
                        if (quotes.isEmpty()) error = "No quotes"
                    }
                } else error = "Search failed"
            } catch (e: Exception) { error = e.message }
            isLoading = false
        }
    }

    fun confirmBooking(quote: Quote, quantity: Int) {
        isLoading = true
        error = null
        scope.launch {
            try {
                val resp = ApiClient.instance.confirmBooking(token = token, quoteId = quote.quoteId, request = ConfirmRequest(quantity))
                if (resp.isSuccessful && resp.body() != null) {
                    bookingId = resp.body()?.bookingId
                    currentScreen = Screen.Ticket
                } else error = "Booking failed"
            } catch (e: Exception) { error = e.message }
            isLoading = false
        }
    }

    // Init
    LaunchedEffect(Unit) {
        val savedToken = TokenManager.getToken(context)
        if (savedToken != null && savedToken.isNotEmpty()) {
            token = savedToken
            loadMetroData()
            currentScreen = Screen.Main
        } else if (TokenManager.isOnboardingDone(context)) {
            currentScreen = Screen.Auth
        }
    }

    // THEME FIX: key on the outermost level
    key(themeManager.currentTheme, currentScreen, selectedTab) {
        CompositionLocalProvider(LocalThemeManager provides themeManager) {
            Box(modifier = Modifier.fillMaxSize().background(themeManager.bg)) {
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
                                onThemeToggle = { themeManager.toggle() },
                                onLogout = {
                                    token = ""
                                    TokenManager.clearToken(context)
                                    currentScreen = Screen.Auth
                                }
                            )
                        }
                        // Bottom nav
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            BottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
                        }
                    }
                    is Screen.SourcePicker -> {
                        StationPickerScreen(
                            stations = stations,
                            onStationSelected = { station ->
                                selectedSource = station
                                CacheManager.addRecentStation(context, station)
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
                                CacheManager.addRecentStation(context, station)
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
                    is Screen.Ticket -> {
                        TicketScreen(
                            token = token,
                            bookingId = bookingId ?: "",
                            onBack = {
                                currentScreen = Screen.Main
                                bookingId = null
                            }
                        )
                    }
                }
            }
        }
    }
}
