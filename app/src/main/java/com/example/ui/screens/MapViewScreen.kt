package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveBusPosition
import com.example.data.model.BusStop
import com.example.ui.theme.OceanBlueSecondary
import com.example.ui.theme.TitsaGreenPrimary
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
    userLat: Double,
    userLng: Double,
    stops: List<BusStop>,
    activeBuses: List<ActiveBusPosition>,
    onStopSelected: (BusStop) -> Unit,
    onRequestGps: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMapStop by remember { mutableStateOf<BusStop?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Map Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mapa de Tenerife & GPS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Paradas y guaguas activas en tiempo real",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    onClick = onRequestGps,
                    shape = RoundedCornerShape(12.dp),
                    color = TitsaGreenPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("map_recenter_gps_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "Recentrar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mi Ubicación", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Interactive Canvas Map
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE2ECE6))
                    .pointerInput(stops) {
                        detectTapGestures { tapOffset ->
                            // Map tap coordinates to nearby stop nodes
                            val width = size.width.toFloat()
                            val height = size.height.toFloat()
                            val center = Offset(width / 2f, height / 2f)

                            val tappedStop = stops.minByOrNull { stop ->
                                val dx = (stop.longitude - userLng) * 12000f
                                val dy = -(stop.latitude - userLat) * 12000f
                                val stopPos = Offset(center.x + dx.toFloat(), center.y + dy.toFloat())
                                (stopPos - tapOffset).getDistance()
                            }

                            if (tappedStop != null) {
                                val dx = (tappedStop.longitude - userLng) * 12000f
                                val dy = -(tappedStop.latitude - userLat) * 12000f
                                val stopPos = Offset(center.x + dx.toFloat(), center.y + dy.toFloat())
                                if ((stopPos - tapOffset).getDistance() < 120f) {
                                    selectedMapStop = tappedStop
                                }
                            }
                        }
                    }
            ) {
                val mapWidth = size.width
                val mapHeight = size.height
                val center = Offset(mapWidth / 2f, mapHeight / 2f)

                // Draw distance radar rings
                drawCircle(
                    color = TitsaGreenPrimary.copy(alpha = 0.15f),
                    radius = 200f,
                    center = center,
                    style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
                drawCircle(
                    color = TitsaGreenPrimary.copy(alpha = 0.1f),
                    radius = 400f,
                    center = center,
                    style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
                )

                // Draw TF-1 / TF-5 Highway simulation lines
                drawLine(
                    color = Color(0xFFA5D6A7),
                    start = Offset(center.x - 500f, center.y + 300f),
                    end = Offset(center.x + 500f, center.y - 300f),
                    strokeWidth = 12f
                )
                drawLine(
                    color = Color(0xFF90CAF9),
                    start = Offset(center.x - 300f, center.y - 400f),
                    end = Offset(center.x + 400f, center.y + 200f),
                    strokeWidth = 10f
                )

                // Draw Bus Stops
                stops.forEach { stop ->
                    val dx = (stop.longitude - userLng) * 12000f
                    val dy = -(stop.latitude - userLat) * 12000f
                    val pos = Offset(center.x + dx.toFloat(), center.y + dy.toFloat())

                    // Stop pin halo
                    drawCircle(
                        color = if (selectedMapStop?.stopCode == stop.stopCode) Color(0xFFFF6D00) else TitsaGreenPrimary,
                        radius = if (selectedMapStop?.stopCode == stop.stopCode) 18f else 12f,
                        center = pos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = if (selectedMapStop?.stopCode == stop.stopCode) 8f else 5f,
                        center = pos
                    )
                }

                // Draw Active Moving Buses
                activeBuses.forEach { bus ->
                    val dx = (bus.longitude - userLng) * 12000f
                    val dy = -(bus.latitude - userLat) * 12000f
                    val busPos = Offset(center.x + dx.toFloat(), center.y + dy.toFloat())

                    drawCircle(
                        color = Color(0xFF008751),
                        radius = 20f,
                        center = busPos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 16f,
                        center = busPos
                    )
                    drawCircle(
                        color = Color(0xFF008751),
                        radius = 12f,
                        center = busPos
                    )
                }

                // Draw User GPS Node
                drawCircle(
                    color = Color(0x330277BD),
                    radius = 36f,
                    center = center
                )
                drawCircle(
                    color = OceanBlueSecondary,
                    radius = 18f,
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = center
                )
            }

            // Legend Overlay (Top Left)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(OceanBlueSecondary))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tu Posición GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TitsaGreenPrimary))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Parada de Guagua", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF008751)))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Guagua Activa (Live)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom Selected Stop Quick Inspector
            selectedMapStop?.let { stop ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                        .testTag("map_selected_stop_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TitsaGreenPrimary,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = "Parada #${stop.stopCode}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "${stop.distanceMeters}m de ti",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanBlueSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stop.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${stop.municipality} • Líneas: ${stop.lines.joinToString(", ")}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onStopSelected(stop) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TitsaGreenPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("map_view_arrivals_button")
                        ) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ver Llegadas en Tiempo Real", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
