package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusStop
import com.example.data.model.LocationPreset
import com.example.data.model.TrafficAlert
import com.example.ui.components.HeaderBanner
import com.example.ui.components.StopCard
import com.example.ui.theme.TitsaGreenPrimary
import com.example.ui.theme.WarningOrange
import com.example.ui.viewmodel.GuaguasUiState

@Composable
fun HomeScreen(
    uiState: GuaguasUiState,
    locationPresets: List<LocationPreset>,
    favoriteStopCodes: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onPresetSelected: (LocationPreset) -> Unit,
    onRequestGps: () -> Unit,
    onStopClick: (BusStop) -> Unit,
    onFavoriteToggle: (BusStop, Boolean) -> Unit,
    onRefresh: () -> Unit,
    onViewAlertsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header Banner with Hero artwork, Presets & Search Bar
        HeaderBanner(
            currentPreset = uiState.currentPreset,
            presets = locationPresets,
            isGpsActive = uiState.isGpsActive,
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onPresetSelected = { preset ->
                onPresetSelected(preset)
            },
            onGpsClicked = onRequestGps
        )

        // Traffic Alert Summary Card (if active alerts exist)
        if (uiState.trafficAlerts.isNotEmpty()) {
            val topAlert = uiState.trafficAlerts.first()
            Card(
                onClick = onViewAlertsClick,
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WarningOrange.copy(alpha = 0.12f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("top_traffic_alert_card")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Aviso Tráfico",
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topAlert.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                        Text(
                            text = "${topAlert.highwayOrZone} • ${topAlert.timestamp}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Ver Alertas",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningOrange
                    )
                }
            }
        }

        // Section Title: Nearby Stops
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = TitsaGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (uiState.searchQuery.isBlank()) "Paradas Cercanas" else "Resultados de Búsqueda",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TitsaGreenPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${uiState.nearbyStops.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitsaGreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier.testTag("refresh_home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar",
                    tint = TitsaGreenPrimary
                )
            }
        }

        // List of Nearby Bus Stops
        if (uiState.nearbyStops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No se encontraron paradas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Intenta buscar por otro nombre de parada o código (ej. 9181, Santa Cruz, 110)",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = uiState.nearbyStops,
                    key = { it.stopCode }
                ) { stop ->
                    val isFav = favoriteStopCodes.contains(stop.stopCode)
                    StopCard(
                        stop = stop,
                        isFavorite = isFav,
                        onStopClick = { onStopClick(stop) },
                        onFavoriteToggle = { onFavoriteToggle(stop, isFav) }
                    )
                }
            }
        }
    }
}
