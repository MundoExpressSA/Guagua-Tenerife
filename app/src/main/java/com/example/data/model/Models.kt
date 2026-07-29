package com.example.data.model

data class LocationPreset(
    val id: String,
    val name: String,
    val municipality: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val isRealGps: Boolean = false
)

data class BusStop(
    val stopCode: String,
    val name: String,
    val municipality: String,
    val zone: String, // e.g., "Santa Cruz - Laguna", "Sur", "Norte", "Anaga - Teide"
    val latitude: Double,
    val longitude: Double,
    val lines: List<String>,
    val distanceMeters: Int = 0,
    val isAccessible: Boolean = true,
    val hasShelter: Boolean = true,
    val address: String = ""
)

data class BusLine(
    val lineNumber: String,
    val name: String,
    val origin: String,
    val destination: String,
    val category: LineCategory,
    val colorHex: String,
    val frequencyMinutesPeak: Int,
    val operatingHours: String,
    val isExpress: Boolean = false
)

enum class LineCategory(val displayName: String, val colorHex: String) {
    EXPRES_SUR("Exprés Sur", "#008751"),
    METROPOLITANA("Metropolitana", "#0277BD"),
    NORTE("Norte - Isla", "#E65100"),
    ANAGA_TEIDE("Anaga y Teide", "#6A1B9A"),
    NOCTURNA("Nocturnas", "#263238")
}

data class BusArrival(
    val id: String,
    val stopCode: String,
    val lineNumber: String,
    val destination: String,
    val minutesLeft: Int,
    val isRealTime: Boolean,
    val delayMinutes: Int = 0, // >0 means delayed, 0 means on time
    val statusMessage: String = "En hora",
    val vehicleId: String = "GUAGUA-${(1000..9999).random()}",
    val isElectric: Boolean = false,
    val occupancyLevel: OccupancyLevel = OccupancyLevel.MEDIUM
)

enum class OccupancyLevel(val label: String) {
    LOW("Baja ocupación"),
    MEDIUM("Ocupación media"),
    HIGH("Ocupación alta")
}

data class TrafficAlert(
    val id: String,
    val title: String,
    val description: String,
    val affectedLines: List<String>,
    val severity: AlertSeverity,
    val highwayOrZone: String,
    val timestamp: String
)

enum class AlertSeverity(val label: String, val colorHex: String) {
    INFO("Aviso Informativo", "#0277BD"),
    MODERATE("Retraso Moderado", "#FF8F00"),
    CRITICAL("Obra / Retraso Grave", "#D32F2F")
}

data class ActiveBusPosition(
    val busId: String,
    val lineNumber: String,
    val destination: String,
    val latitude: Double,
    val longitude: Double,
    val headingDegrees: Float,
    val speedKmh: Int,
    val nextStopName: String,
    val delayMinutes: Int
)
