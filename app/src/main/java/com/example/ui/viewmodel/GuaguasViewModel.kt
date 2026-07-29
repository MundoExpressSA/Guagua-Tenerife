package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.GuaguasRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GuaguasUiState(
    val currentPreset: LocationPreset = LocationPreset(
        id = "sta_cruz",
        name = "Intercambiador Santa Cruz",
        municipality = "Santa Cruz de Tenerife",
        latitude = 28.4589,
        longitude = -16.2572,
        description = "Estación central de guaguas del área metropolitana"
    ),
    val userLat: Double = 28.4589,
    val userLng: Double = -16.2572,
    val isGpsActive: Boolean = false,
    val gpsAddressName: String = "Intercambiador Santa Cruz",
    val searchQuery: String = "",
    val nearbyStops: List<BusStop> = emptyList(),
    val selectedStop: BusStop? = null,
    val selectedStopArrivals: List<BusArrival> = emptyList(),
    val isArrivalsLoading: Boolean = false,
    val trafficAlerts: List<TrafficAlert> = emptyList(),
    val activeBuses: List<ActiveBusPosition> = emptyList(),
    val activeTab: Int = 0, // 0: Cercanas, 1: Mapa, 2: Líneas, 3: Alertas, 4: Favoritas
    val activeLineCategoryFilter: LineCategory? = null,
    val selectedLine: BusLine? = null,
    val activeNotificationMessage: String? = null
)

class GuaguasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuaguasRepository
    val locationPresets: List<LocationPreset>

    private val _uiState = MutableStateFlow(GuaguasUiState())
    val uiState: StateFlow<GuaguasUiState> = _uiState.asStateFlow()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    init {
        val dao = AppDatabase.getDatabase(application).guaguasDao()
        repository = GuaguasRepository(dao)
        locationPresets = repository.locationPresets

        // Initialize state
        refreshNearbyStops()
        _uiState.update {
            it.copy(
                trafficAlerts = repository.getActiveTrafficAlerts(),
                activeBuses = repository.getActiveBusesOnMap()
            )
        }

        // Live real-time ticker to update countdowns and check delay alerts
        startRealTimeTicker()
    }

    val favoriteStopsFlow = repository.favoriteStops
    val delayAlertsFlow = repository.delayAlerts
    val savedLinesFlow = repository.savedLines

    fun selectLocationPreset(preset: LocationPreset, context: Context? = null) {
        if (preset.isRealGps && context != null) {
            requestGpsLocation(context)
        } else {
            _uiState.update {
                it.copy(
                    currentPreset = preset,
                    userLat = preset.latitude,
                    userLng = preset.longitude,
                    isGpsActive = false,
                    gpsAddressName = preset.name
                )
            }
            refreshNearbyStops()
        }
    }

    @SuppressLint("MissingPermission")
    fun requestGpsLocation(context: Context) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val gpsPreset = LocationPreset(
                        id = "gps_active",
                        name = "Ubicación GPS Real",
                        municipality = "Tenerife (GPS)",
                        latitude = location.latitude,
                        longitude = location.longitude,
                        description = "Coordenadas: ${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)}",
                        isRealGps = true
                    )
                    _uiState.update {
                        it.copy(
                            currentPreset = gpsPreset,
                            userLat = location.latitude,
                            userLng = location.longitude,
                            isGpsActive = true,
                            gpsAddressName = "Ubicación GPS (${String.format("%.3f", location.latitude)}, ${String.format("%.3f", location.longitude)})"
                        )
                    }
                    refreshNearbyStops()
                } else {
                    triggerSimulatedNotification("No se pudo obtener la posición GPS exacta. Usando Intercambiador Santa Cruz.")
                }
            }
        } catch (e: Exception) {
            triggerSimulatedNotification("Permiso de GPS requerido o desactivado.")
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshNearbyStops()
    }

    fun selectStop(stop: BusStop) {
        _uiState.update {
            it.copy(
                selectedStop = stop,
                isArrivalsLoading = true
            )
        }
        refreshSelectedStopArrivals(stop.stopCode)
    }

    fun clearSelectedStop() {
        _uiState.update { it.copy(selectedStop = null, selectedStopArrivals = emptyList()) }
    }

    fun selectLine(line: BusLine?) {
        _uiState.update { it.copy(selectedLine = line) }
    }

    fun setCategoryFilter(category: LineCategory?) {
        _uiState.update { it.copy(activeLineCategoryFilter = category) }
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun isStopFavorite(stopCode: String): Flow<Boolean> {
        return repository.isStopFavorite(stopCode)
    }

    fun toggleFavorite(stop: BusStop, isCurrentlyFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteStop(stop, isCurrentlyFav)
            val msg = if (isCurrentlyFav) "Parada ${stop.stopCode} eliminada de favoritas"
                      else "Parada ${stop.stopCode} (${stop.name}) guardada en favoritas"
            triggerSimulatedNotification(msg)
        }
    }

    fun toggleDelayAlert(lineNumber: String, lineName: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleDelayAlert(lineNumber, lineName, isEnabled)
            val msg = if (isEnabled) "Notificaciones de retrasos activadas para Línea $lineNumber"
                      else "Alertas desactivadas para Línea $lineNumber"
            triggerSimulatedNotification(msg)
        }
    }

    fun toggleSavedLine(line: BusLine, isSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSavedLine(line, isSaved)
            val msg = if (isSaved) "Línea ${line.lineNumber} eliminada" else "Línea ${line.lineNumber} guardada"
            triggerSimulatedNotification(msg)
        }
    }

    fun triggerSimulatedDelayAlert(lineNumber: String = "110") {
        val alertText = "⚠️ NOTIFICACIÓN EN TIEMPO REAL: La Guagua de la Línea $lineNumber registra un retraso de +6 min por retención en la autopista TF-5."
        triggerSimulatedNotification(alertText)
    }

    fun dismissNotification() {
        _uiState.update { it.copy(activeNotificationMessage = null) }
    }

    private fun triggerSimulatedNotification(message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(activeNotificationMessage = message) }
            delay(5000)
            _uiState.update {
                if (it.activeNotificationMessage == message) it.copy(activeNotificationMessage = null)
                else it
            }
        }
    }

    fun refreshNearbyStops() {
        val state = _uiState.value
        val stops = repository.getStopsNearLocation(
            lat = state.userLat,
            lng = state.userLng,
            query = state.searchQuery
        )
        _uiState.update { it.copy(nearbyStops = stops) }
    }

    fun refreshSelectedStopArrivals(stopCode: String? = _uiState.value.selectedStop?.stopCode) {
        if (stopCode == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isArrivalsLoading = true) }
            val arrivals = repository.getRealTimeArrivals(stopCode)
            _uiState.update {
                it.copy(
                    selectedStopArrivals = arrivals,
                    isArrivalsLoading = false
                )
            }
        }
    }

    private fun startRealTimeTicker() {
        viewModelScope.launch {
            while (true) {
                delay(15000) // update real-time timer every 15s
                val currentStop = _uiState.value.selectedStop
                if (currentStop != null) {
                    refreshSelectedStopArrivals(currentStop.stopCode)
                }
            }
        }
    }

    fun getAllLines(): List<BusLine> {
        val cat = _uiState.value.activeLineCategoryFilter
        return if (cat == null) repository.allLines
        else repository.allLines.filter { it.category == cat }
    }
}
