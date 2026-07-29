package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.NotificationBanner
import com.example.ui.screens.*
import com.example.ui.theme.GuaguasTenerifeTheme
import com.example.ui.theme.TitsaGreenPrimary
import com.example.ui.viewmodel.GuaguasViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GuaguasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GuaguasTenerifeTheme {
                GuaguasApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuaguasApp(viewModel: GuaguasViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val favoriteStops by viewModel.favoriteStopsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val delayAlerts by viewModel.delayAlertsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val savedLines by viewModel.savedLinesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    val favoriteStopCodes = remember(favoriteStops) { favoriteStops.map { it.stopCode } }
    val savedLineNumbers = remember(savedLines) { savedLines.map { it.lineNumber } }

    // Runtime Permission Launcher for GPS Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            viewModel.requestGpsLocation(context)
        }
    }

    fun handleGpsRequest() {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            viewModel.requestGpsLocation(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (uiState.selectedStop == null) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = uiState.activeTab == 0,
                            onClick = { viewModel.setActiveTab(0) },
                            icon = { Icon(Icons.Default.NearMe, contentDescription = "Cercanas") },
                            label = { Text("Cercanas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TitsaGreenPrimary,
                                selectedTextColor = TitsaGreenPrimary,
                                indicatorColor = TitsaGreenPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_cercanas")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == 1,
                            onClick = { viewModel.setActiveTab(1) },
                            icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                            label = { Text("Mapa", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TitsaGreenPrimary,
                                selectedTextColor = TitsaGreenPrimary,
                                indicatorColor = TitsaGreenPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_mapa")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == 2,
                            onClick = { viewModel.setActiveTab(2) },
                            icon = { Icon(Icons.Default.DirectionsBus, contentDescription = "Líneas") },
                            label = { Text("Líneas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TitsaGreenPrimary,
                                selectedTextColor = TitsaGreenPrimary,
                                indicatorColor = TitsaGreenPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_lineas")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == 3,
                            onClick = { viewModel.setActiveTab(3) },
                            icon = { Icon(Icons.Default.NotificationsActive, contentDescription = "Alertas") },
                            label = { Text("Alertas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TitsaGreenPrimary,
                                selectedTextColor = TitsaGreenPrimary,
                                indicatorColor = TitsaGreenPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_alertas")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == 4,
                            onClick = { viewModel.setActiveTab(4) },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritas") },
                            label = { Text("Favoritas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TitsaGreenPrimary,
                                selectedTextColor = TitsaGreenPrimary,
                                indicatorColor = TitsaGreenPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_favoritas")
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Main Tab Content
                when (uiState.activeTab) {
                    0 -> HomeScreen(
                        uiState = uiState,
                        locationPresets = viewModel.locationPresets,
                        favoriteStopCodes = favoriteStopCodes,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onPresetSelected = { preset -> viewModel.selectLocationPreset(preset, context) },
                        onRequestGps = { handleGpsRequest() },
                        onStopClick = viewModel::selectStop,
                        onFavoriteToggle = viewModel::toggleFavorite,
                        onRefresh = viewModel::refreshNearbyStops,
                        onViewAlertsClick = { viewModel.setActiveTab(3) }
                    )

                    1 -> MapViewScreen(
                        userLat = uiState.userLat,
                        userLng = uiState.userLng,
                        stops = uiState.nearbyStops,
                        activeBuses = uiState.activeBuses,
                        onStopSelected = viewModel::selectStop,
                        onRequestGps = { handleGpsRequest() }
                    )

                    2 -> LineExplorerScreen(
                        lines = viewModel.getAllLines(),
                        activeCategory = uiState.activeLineCategoryFilter,
                        savedLineNumbers = savedLineNumbers,
                        onCategorySelect = viewModel::setCategoryFilter,
                        onLineSelect = viewModel::selectLine,
                        onSavedLineToggle = viewModel::toggleSavedLine,
                        onSimulateDelayClick = { lineNum -> viewModel.triggerSimulatedDelayAlert(lineNum) }
                    )

                    3 -> AlertsScreen(
                        alerts = uiState.trafficAlerts,
                        subscribedLineAlerts = delayAlerts,
                        onToggleLineAlert = viewModel::toggleDelayAlert,
                        onSimulateTestNotification = { viewModel.triggerSimulatedDelayAlert("110") }
                    )

                    4 -> FavoritesScreen(
                        favoriteStops = favoriteStops,
                        savedLines = savedLines,
                        onStopClick = { stopCode ->
                            val stop = uiState.nearbyStops.find { it.stopCode == stopCode }
                            if (stop != null) viewModel.selectStop(stop)
                        },
                        onDeleteFavoriteStop = { fav ->
                            val stop = uiState.nearbyStops.find { it.stopCode == fav.stopCode }
                            if (stop != null) viewModel.toggleFavorite(stop, true)
                        },
                        onDeleteSavedLine = { line ->
                            val busLine = viewModel.getAllLines().find { it.lineNumber == line.lineNumber }
                            if (busLine != null) viewModel.toggleSavedLine(busLine, true)
                        }
                    )
                }

                // Stop Detail Live Arrivals Overlay Screen
                uiState.selectedStop?.let { stop ->
                    val isFav = favoriteStopCodes.contains(stop.stopCode)
                    StopDetailScreen(
                        stop = stop,
                        arrivals = uiState.selectedStopArrivals,
                        isLoading = uiState.isArrivalsLoading,
                        isFavorite = isFav,
                        onBackClick = viewModel::clearSelectedStop,
                        onRefresh = { viewModel.refreshSelectedStopArrivals(stop.stopCode) },
                        onFavoriteToggle = { viewModel.toggleFavorite(stop, isFav) },
                        onSimulateDelayClick = { lineNum -> viewModel.triggerSimulatedDelayAlert(lineNum) }
                    )
                }
            }
        }

        // Top Floating Push Alert Banner
        NotificationBanner(
            message = uiState.activeNotificationMessage,
            onDismiss = viewModel::dismissNotification,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 8.dp)
        )
    }
}
