package com.pandatern.makco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.pandatern.makco.data.model.*
import com.pandatern.makco.data.remote.ApiClient
import com.pandatern.makco.ui.navigation.MakcoNavHost
import com.pandatern.makco.ui.theme.MakcoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MakcoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var stations by remember { mutableStateOf<List<Station>>(emptyList()) }
                    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }

                    LaunchedEffect(Unit) {
                        // Load stations and routes
                        try {
                            // TODO: Get token from auth flow
                            val tempToken = "29b7452c-5bb4-463e-97da-44c6eda3b37e"
                            stations = ApiClient.instance.getStations(tempToken)
                            routes = ApiClient.instance.getRoutes(tempToken)
                        } catch (e: Exception) {
                            // Handle error
                        }
                    }

                    MakcoNavHost(
                        stations = stations,
                        routes = routes
                    )
                }
            }
        }
    }
}
