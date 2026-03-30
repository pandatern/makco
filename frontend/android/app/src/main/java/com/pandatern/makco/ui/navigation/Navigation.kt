package com.pandatern.makco.ui.navigation

import androidx.compose.runtime.*
import com.pandatern.makco.data.model.*
import com.pandatern.makco.ui.screens.BookingScreen
import com.pandatern.makco.ui.screens.HomeScreen
import com.pandatern.makco.ui.screens.PaymentScreen
import com.pandatern.makco.ui.screens.StationPickerScreen

sealed class Screen {
    object Home : Screen()
    object SourcePicker : Screen()
    object DestinationPicker : Screen()
    object Booking : Screen()
    object Payment : Screen()
}

@Composable
fun MakcoNavHost(
    stations: List<Station>,
    routes: List<Route>
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var selectedSource by remember { mutableStateOf<Station?>(null) }
    var selectedDestination by remember { mutableStateOf<Station?>(null) }
    var quotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }
    var bookingId by remember { mutableStateOf<String?>(null) }
    var bookingStatus by remember { mutableStateOf<BookingStatus?>(null) }

    when (currentScreen) {
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
                onConfirm = { quote ->
                    selectedQuote = quote
                    currentScreen = Screen.Payment
                },
                onBack = { currentScreen = Screen.Home }
            )
        }
        is Screen.Payment -> {
            PaymentScreen(
                bookingStatus = bookingStatus,
                onPayClick = {
                    // TODO: Open Juspay payment
                },
                onBack = { currentScreen = Screen.Booking }
            )
        }
    }
}
