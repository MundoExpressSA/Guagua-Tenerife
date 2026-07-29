package com.example.data.repository

import com.example.data.db.DelayAlertEntity
import com.example.data.db.FavoriteStopEntity
import com.example.data.db.GuaguasDao
import com.example.data.db.SavedLineEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlin.math.*

class GuaguasRepository(private val guaguasDao: GuaguasDao) {

    val favoriteStops: Flow<List<FavoriteStopEntity>> = guaguasDao.getAllFavoriteStops()
    val delayAlerts: Flow<List<DelayAlertEntity>> = guaguasDao.getAllDelayAlerts()
    val savedLines: Flow<List<SavedLineEntity>> = guaguasDao.getAllSavedLines()

    suspend fun toggleFavoriteStop(stop: BusStop, isCurrentlyFav: Boolean) {
        if (isCurrentlyFav) {
            guaguasDao.deleteFavoriteStop(stop.stopCode)
        } else {
            guaguasDao.insertFavoriteStop(
                FavoriteStopEntity(
                    stopCode = stop.stopCode,
                    stopName = stop.name,
                    municipality = stop.municipality,
                    linesJoined = stop.lines.joinToString(", "),
                    latitude = stop.latitude,
                    longitude = stop.longitude
                )
            )
        }
    }

    fun isStopFavorite(stopCode: String): Flow<Boolean> {
        return guaguasDao.isStopFavorite(stopCode)
    }

    suspend fun toggleDelayAlert(lineNumber: String, lineName: String, isEnabled: Boolean) {
        if (isEnabled) {
            guaguasDao.insertDelayAlert(
                DelayAlertEntity(
                    lineCode = lineNumber,
                    lineName = lineName,
                    isEnabled = true
                )
            )
        } else {
            guaguasDao.deleteDelayAlert(lineNumber)
        }
    }

    suspend fun toggleSavedLine(line: BusLine, isSaved: Boolean) {
        if (isSaved) {
            guaguasDao.deleteSavedLine(line.lineNumber)
        } else {
            guaguasDao.insertSavedLine(
                SavedLineEntity(
                    lineNumber = line.lineNumber,
                    originDestination = "${line.origin} ➔ ${line.destination}",
                    category = line.category.displayName,
                    colorHex = line.colorHex
                )
            )
        }
    }

    // Default Tenerife Location Presets
    val locationPresets = listOf(
        LocationPreset(
            id = "gps",
            name = "Ubicación GPS Actual",
            municipality = "Detectando mediante GPS...",
            latitude = 28.4636,
            longitude = -16.2518,
            description = "Coordenadas GPS de tu dispositivo",
            isRealGps = true
        ),
        LocationPreset(
            id = "sta_cruz",
            name = "Intercambiador Santa Cruz",
            municipality = "Santa Cruz de Tenerife",
            latitude = 28.4589,
            longitude = -16.2572,
            description = "Estación central de guaguas del área metropolitana"
        ),
        LocationPreset(
            id = "la_laguna",
            name = "Intercambiador La Laguna",
            municipality = "San Cristóbal de La Laguna",
            latitude = 28.4812,
            longitude = -16.3211,
            description = "Eje conector metropolitano y tranvía"
        ),
        LocationPreset(
            id = "costa_adeje",
            name = "Estación Costa Adeje",
            municipality = "Adeje / Arona",
            latitude = 28.0772,
            longitude = -16.7321,
            description = "Hub principal de guaguas del Sur de Tenerife"
        ),
        LocationPreset(
            id = "puerto_cruz",
            name = "Estación Puerto de la Cruz",
            municipality = "Puerto de la Cruz",
            latitude = 28.4144,
            longitude = -16.5510,
            description = "Estación central del Norte de la isla"
        ),
        LocationPreset(
            id = "tfs_airport",
            name = "Aeropuerto Tenerife Sur (TFS)",
            municipality = "Granadilla de Abona",
            latitude = 28.0445,
            longitude = -16.5725,
            description = "Parada de llegadas y salidas de vuelos internacionales"
        ),
        LocationPreset(
            id = "tfn_airport",
            name = "Aeropuerto Tenerife Norte (TFN)",
            municipality = "San Cristóbal de La Laguna",
            latitude = 28.4827,
            longitude = -16.3415,
            description = "Aeropuerto Ciudad de La Laguna"
        ),
        LocationPreset(
            id = "teide",
            name = "Teleférico del Teide",
            municipality = "Parque Nacional del Teide",
            latitude = 28.2547,
            longitude = -16.6456,
            description = "Acceso a la cumbre y líneas 342 y 348"
        )
    )

    // Master List of TITSA Bus Stops in Tenerife
    private val allStops = listOf(
        BusStop(
            stopCode = "9181",
            name = "Intercambiador Santa Cruz (Dársena Sur)",
            municipality = "Santa Cruz de Tenerife",
            zone = "Metropolitana",
            latitude = 28.4589,
            longitude = -16.2572,
            lines = listOf("110", "111", "014", "102", "103", "905", "711"),
            address = "Av. Tres de Mayo s/n"
        ),
        BusStop(
            stopCode = "9182",
            name = "Intercambiador Santa Cruz (Dársena Norte)",
            municipality = "Santa Cruz de Tenerife",
            zone = "Metropolitana",
            latitude = 28.4595,
            longitude = -16.2568,
            lines = listOf("101", "102", "108", "015", "934"),
            address = "Planta Alta Intercambiador"
        ),
        BusStop(
            stopCode = "1204",
            name = "Plaza de España - Cabildo",
            municipality = "Santa Cruz de Tenerife",
            zone = "Metropolitana",
            latitude = 28.4671,
            longitude = -16.2472,
            lines = listOf("903", "905", "946", "910"),
            address = "Av. Marítima"
        ),
        BusStop(
            stopCode = "9133",
            name = "Intercambiador La Laguna (Padre Anchieta)",
            municipality = "La Laguna",
            zone = "Metropolitana",
            latitude = 28.4812,
            longitude = -16.3211,
            lines = listOf("014", "015", "101", "102", "343", "050"),
            address = "Rotonda Padre Anchieta"
        ),
        BusStop(
            stopCode = "2145",
            name = "Trinidad - Campus Anchieta",
            municipality = "La Laguna",
            zone = "Metropolitana",
            latitude = 28.4835,
            longitude = -16.3180,
            lines = listOf("014", "015", "050", "203"),
            address = "Av. Trinidad 42"
        ),
        BusStop(
            stopCode = "7120",
            name = "Estación Costa Adeje",
            municipality = "Adeje",
            zone = "Sur",
            latitude = 28.0772,
            longitude = -16.7321,
            lines = listOf("110", "111", "342", "467", "473", "711"),
            address = "Av. de los Pueblos"
        ),
        BusStop(
            stopCode = "7150",
            name = "Los Cristianos (Avenida de Atenas)",
            municipality = "Arona",
            zone = "Sur",
            latitude = 28.0521,
            longitude = -16.7118,
            lines = listOf("110", "111", "470", "483", "711"),
            address = "Av. de Chayofita"
        ),
        BusStop(
            stopCode = "7300",
            name = "Aeropuerto Tenerife Sur (Llegadas / Arrivals)",
            municipality = "Granadilla de Abona",
            zone = "Sur",
            latitude = 28.0445,
            longitude = -16.5725,
            lines = listOf("111", "343", "415", "711"),
            address = "Terminal Salidas/Llegadas TFS"
        ),
        BusStop(
            stopCode = "4102",
            name = "Aeropuerto Tenerife Norte (TFN)",
            municipality = "La Laguna",
            zone = "Metropolitana",
            latitude = 28.4827,
            longitude = -16.3415,
            lines = listOf("102", "108", "343", "020"),
            address = "Planta Principal TFN"
        ),
        BusStop(
            stopCode = "3100",
            name = "Estación Puerto de la Cruz (El Díaz)",
            municipality = "Puerto de la Cruz",
            zone = "Norte",
            latitude = 28.4144,
            longitude = -16.5510,
            lines = listOf("102", "103", "343", "348", "363", "381"),
            address = "Calle Hermanos Fernández Perdigón"
        ),
        BusStop(
            stopCode = "3150",
            name = "Plaza del Charco",
            municipality = "Puerto de la Cruz",
            zone = "Norte",
            latitude = 28.4172,
            longitude = -16.5532,
            lines = listOf("381", "382", "383"),
            address = "C/ Valois"
        ),
        BusStop(
            stopCode = "3201",
            name = "La Orotava (Plaza del Ayuntamiento)",
            municipality = "La Orotava",
            zone = "Norte",
            latitude = 28.3891,
            longitude = -16.5244,
            lines = listOf("101", "345", "348", "380"),
            address = "Av. Canarias"
        ),
        BusStop(
            stopCode = "5010",
            name = "Candelaria - Caletillas",
            municipality = "Candelaria",
            zone = "Sur",
            latitude = 28.3582,
            longitude = -16.3712,
            lines = listOf("111", "120", "122", "128"),
            address = "Autopista TF-1 Enlace Candelaria"
        ),
        BusStop(
            stopCode = "8020",
            name = "Icod de los Vinos (Estación Drago)",
            municipality = "Icod de los Vinos",
            zone = "Norte",
            latitude = 28.3689,
            longitude = -16.7188,
            lines = listOf("363", "325", "354", "360"),
            address = "Av. Chinchanayros"
        ),
        BusStop(
            stopCode = "8050",
            name = "Buenavista del Norte (Estación)",
            municipality = "Buenavista",
            zone = "Norte",
            latitude = 28.3712,
            longitude = -16.8510,
            lines = listOf("363", "355", "369"),
            address = "C/ La Alhóndiga"
        ),
        BusStop(
            stopCode = "9901",
            name = "Parador Nacional de Las Cañadas del Teide",
            municipality = "Parque Nacional del Teide",
            zone = "Anaga - Teide",
            latitude = 28.2234,
            longitude = -16.6272,
            lines = listOf("342", "348"),
            address = "TF-21 Km 42"
        ),
        BusStop(
            stopCode = "9410",
            name = "San Andrés - Playa de Las Teresitas",
            municipality = "Santa Cruz de Tenerife",
            zone = "Metropolitana",
            latitude = 28.5021,
            longitude = -16.1882,
            lines = listOf("910", "946"),
            address = "Av. Marítima San Andrés"
        )
    )

    // Master List of TITSA Bus Lines
    val allLines = listOf(
        BusLine(
            lineNumber = "110",
            name = "Exprés Directo Sur",
            origin = "Intercambiador Sta. Cruz",
            destination = "Estación Costa Adeje",
            category = LineCategory.EXPRES_SUR,
            colorHex = "#008751",
            frequencyMinutesPeak = 15,
            operatingHours = "06:00 - 23:30",
            isExpress = true
        ),
        BusLine(
            lineNumber = "111",
            name = "Sur Autopista - TFS Aeropuerto",
            origin = "Sta. Cruz",
            destination = "Costa Adeje / TFS / Los Cristianos",
            category = LineCategory.EXPRES_SUR,
            colorHex = "#00A664",
            frequencyMinutesPeak = 20,
            operatingHours = "05:00 - 01:30"
        ),
        BusLine(
            lineNumber = "343",
            name = "Exprés Aeropuertos & Puerto Cruz",
            origin = "Puerto de la Cruz",
            destination = "TFN - TFS - Los Cristianos",
            category = LineCategory.EXPRES_SUR,
            colorHex = "#008751",
            frequencyMinutesPeak = 60,
            operatingHours = "06:30 - 21:45",
            isExpress = true
        ),
        BusLine(
            lineNumber = "014",
            name = "Metropolitana Directa",
            origin = "Intercambiador Sta. Cruz",
            destination = "Intercambiador La Laguna",
            category = LineCategory.METROPOLITANA,
            colorHex = "#0277BD",
            frequencyMinutesPeak = 10,
            operatingHours = "05:15 - 00:00"
        ),
        BusLine(
            lineNumber = "015",
            name = "Metropolitana Autopista TF-5",
            origin = "Sta. Cruz",
            destination = "La Laguna (Anchieta)",
            category = LineCategory.METROPOLITANA,
            colorHex = "#0288D1",
            frequencyMinutesPeak = 12,
            operatingHours = "06:00 - 23:00"
        ),
        BusLine(
            lineNumber = "102",
            name = "Exprés Norte - TFN Aeropuerto",
            origin = "Sta. Cruz",
            destination = "TFN Aeropuerto - Puerto Cruz",
            category = LineCategory.NORTE,
            colorHex = "#E65100",
            frequencyMinutesPeak = 30,
            operatingHours = "06:00 - 23:15",
            isExpress = true
        ),
        BusLine(
            lineNumber = "103",
            name = "Directo Norte TF-5",
            origin = "Intercambiador Sta. Cruz",
            destination = "Puerto de la Cruz (Directo)",
            category = LineCategory.NORTE,
            colorHex = "#F57C00",
            frequencyMinutesPeak = 30,
            operatingHours = "06:15 - 22:30",
            isExpress = true
        ),
        BusLine(
            lineNumber = "101",
            name = "Norte Carretera General",
            origin = "La Laguna",
            destination = "La Orotava - Puerto de la Cruz",
            category = LineCategory.NORTE,
            colorHex = "#EF6C00",
            frequencyMinutesPeak = 25,
            operatingHours = "06:00 - 22:00"
        ),
        BusLine(
            lineNumber = "363",
            name = "Norte Isla Baja",
            origin = "Puerto de la Cruz",
            destination = "Icod - Buenavista del Norte",
            category = LineCategory.NORTE,
            colorHex = "#E65100",
            frequencyMinutesPeak = 30,
            operatingHours = "05:45 - 22:15"
        ),
        BusLine(
            lineNumber = "342",
            name = "Ruta Cañadas del Teide Sur",
            origin = "Costa Adeje / Los Cristianos",
            destination = "Teleférico del Teide",
            category = LineCategory.ANAGA_TEIDE,
            colorHex = "#6A1B9A",
            frequencyMinutesPeak = 120,
            operatingHours = "09:15 - 15:30"
        ),
        BusLine(
            lineNumber = "348",
            name = "Ruta Cañadas del Teide Norte",
            origin = "Puerto de la Cruz",
            destination = "Teleférico / Parador del Teide",
            category = LineCategory.ANAGA_TEIDE,
            colorHex = "#7B1FA2",
            frequencyMinutesPeak = 120,
            operatingHours = "09:30 - 16:00"
        ),
        BusLine(
            lineNumber = "711",
            name = "Nocturna Sur Directa",
            origin = "Sta. Cruz",
            destination = "TFS Aeropuerto - Costa Adeje",
            category = LineCategory.NOCTURNA,
            colorHex = "#263238",
            frequencyMinutesPeak = 60,
            operatingHours = "23:30 - 05:30"
        )
    )

    // Current Traffic Alerts on Tenerife Highways
    fun getActiveTrafficAlerts(): List<TrafficAlert> {
        return listOf(
            TrafficAlert(
                id = "alert_tf5_1",
                title = "Atasco en Carril Bus TF-5 (Entrada Santa Cruz)",
                description = "Retrasos de 6-12 min entre Guamasa y Padre Anchieta por elevada densidad de tráfico en hora punta.",
                affectedLines = listOf("102", "103", "015", "108", "101"),
                severity = AlertSeverity.MODERATE,
                highwayOrZone = "Autopista TF-5 Norte",
                timestamp = "Hace 5 min"
            ),
            TrafficAlert(
                id = "alert_tf1_2",
                title = "Obras Nocturnas en Asfalto Enlace TFS",
                description = "Tráfico lento a la altura del Aeropuerto Tenerife Sur en dirección Los Cristianos. Precaución línea 111 y 711.",
                affectedLines = listOf("110", "111", "343", "711"),
                severity = AlertSeverity.INFO,
                highwayOrZone = "Autopista TF-1 Sur",
                timestamp = "Hace 18 min"
            ),
            TrafficAlert(
                id = "alert_teide_3",
                title = "Servicio Teide 342 y 348 Normalizado",
                description = "Accesos por la TF-21 y TF-24 despejados. Guaguas con destino al Parque Nacional operando en horario regular.",
                affectedLines = listOf("342", "348"),
                severity = AlertSeverity.INFO,
                highwayOrZone = "TF-21 Cañadas del Teide",
                timestamp = "Hace 1 hora"
            )
        )
    }

    // Get stops ordered by distance from user coordinates
    fun getStopsNearLocation(lat: Double, lng: Double, query: String = ""): List<BusStop> {
        return allStops
            .map { stop ->
                val dist = calculateHaversineDistanceMeters(lat, lng, stop.latitude, stop.longitude)
                stop.copy(distanceMeters = dist)
            }
            .filter { stop ->
                if (query.isBlank()) true
                else {
                    stop.name.contains(query, ignoreCase = true) ||
                    stop.stopCode.contains(query) ||
                    stop.municipality.contains(query, ignoreCase = true) ||
                    stop.lines.any { it.equals(query.trim(), ignoreCase = true) }
                }
            }
            .sortedBy { it.distanceMeters }
    }

    fun getStopByCode(stopCode: String): BusStop? {
        return allStops.find { it.stopCode == stopCode }
    }

    // Get live arrival predictions for a specific bus stop
    fun getRealTimeArrivals(stopCode: String): List<BusArrival> {
        val stop = getStopByCode(stopCode) ?: return emptyList()
        val arrivals = mutableListOf<BusArrival>()

        stop.lines.forEachIndexed { index, lineNum ->
            val line = allLines.find { it.lineNumber == lineNum }
            val dest = line?.destination ?: "Destino Tenerife"
            
            // Generate primary coming bus
            val baseMins = (index * 4 + 2) % 25 + 1
            val delay = if (lineNum == "102" || lineNum == "103" || lineNum == "015") 4 else 0
            val status = if (delay > 0) "+$delay min retraso TF-5" else "En hora (GPS Live)"

            arrivals.add(
                BusArrival(
                    id = "arr_${stopCode}_${lineNum}_1",
                    stopCode = stopCode,
                    lineNumber = lineNum,
                    destination = dest,
                    minutesLeft = baseMins,
                    isRealTime = true,
                    delayMinutes = delay,
                    statusMessage = status,
                    isElectric = lineNum == "014" || lineNum == "905",
                    occupancyLevel = if (baseMins <= 5) OccupancyLevel.HIGH else OccupancyLevel.LOW
                )
            )

            // Generate secondary coming bus
            val secondMins = baseMins + (line?.frequencyMinutesPeak ?: 15)
            arrivals.add(
                BusArrival(
                    id = "arr_${stopCode}_${lineNum}_2",
                    stopCode = stopCode,
                    lineNumber = lineNum,
                    destination = dest,
                    minutesLeft = secondMins,
                    isRealTime = true,
                    delayMinutes = 0,
                    statusMessage = "Programada",
                    occupancyLevel = OccupancyLevel.MEDIUM
                )
            )
        }

        return arrivals.sortedBy { it.minutesLeft }
    }

    // Active live buses simulated positions on Tenerife map coordinates
    fun getActiveBusesOnMap(): List<ActiveBusPosition> {
        return listOf(
            ActiveBusPosition(
                busId = "TITSA-5012",
                lineNumber = "110",
                destination = "Estación Costa Adeje",
                latitude = 28.2341,
                longitude = -16.4821,
                headingDegrees = 220f,
                speedKmh = 92,
                nextStopName = "Candelaria - Caletillas",
                delayMinutes = 0
            ),
            ActiveBusPosition(
                busId = "TITSA-5018",
                lineNumber = "110",
                destination = "Intercambiador Sta. Cruz",
                latitude = 28.0982,
                longitude = -16.7112,
                headingDegrees = 40f,
                speedKmh = 88,
                nextStopName = "Intercambiador Santa Cruz",
                delayMinutes = 0
            ),
            ActiveBusPosition(
                busId = "TITSA-3104",
                lineNumber = "102",
                destination = "Puerto de la Cruz",
                latitude = 28.4610,
                longitude = -16.3312,
                headingDegrees = 280f,
                speedKmh = 45,
                nextStopName = "TFN Aeropuerto",
                delayMinutes = 5
            ),
            ActiveBusPosition(
                busId = "TITSA-2090",
                lineNumber = "014",
                destination = "Intercambiador La Laguna",
                latitude = 28.4721,
                longitude = -16.2912,
                headingDegrees = 300f,
                speedKmh = 38,
                nextStopName = "Trinidad Padre Anchieta",
                delayMinutes = 0
            ),
            ActiveBusPosition(
                busId = "TITSA-6012",
                lineNumber = "343",
                destination = "TFS Aeropuerto",
                latitude = 28.1210,
                longitude = -16.5910,
                headingDegrees = 180f,
                speedKmh = 85,
                nextStopName = "TFS Llegadas",
                delayMinutes = 2
            )
        )
    }

    // Distance calculation formula
    private fun calculateHaversineDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Int {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toInt()
    }
}
