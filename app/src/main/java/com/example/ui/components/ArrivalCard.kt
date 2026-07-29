package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusArrival
import com.example.data.model.OccupancyLevel
import com.example.ui.theme.TitsaGreenPrimary
import com.example.ui.theme.WarningOrange

@Composable
fun ArrivalCard(
    arrival: BusArrival,
    modifier: Modifier = Modifier
) {
    // Pulsating animation for real-time live indicator dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (arrival.delayMinutes > 0) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("arrival_card_${arrival.lineNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Line Number Badge & Destination
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getLineColor(arrival.lineNumber),
                    contentColor = Color.White
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = arrival.lineNumber,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = arrival.destination,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Status line / delay message
                    if (arrival.delayMinutes > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Retraso",
                                tint = WarningOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = arrival.statusMessage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningOrange
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(TitsaGreenPrimary.copy(alpha = alpha))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = arrival.statusMessage,
                                fontSize = 12.sp,
                                color = TitsaGreenPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Occupancy & Electric badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = arrival.occupancyLevel.label,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        if (arrival.isElectric) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ElectricCar,
                                contentDescription = "Eléctrica",
                                tint = TitsaGreenPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "100% Eco",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TitsaGreenPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Column: Live Arrival Countdown Timer
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (arrival.minutesLeft <= 3) TitsaGreenPrimary else MaterialTheme.colorScheme.surface,
                    contentColor = if (arrival.minutesLeft <= 3) Color.White else MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (arrival.minutesLeft == 0) "¡LLEGANDO!" else "${arrival.minutesLeft} min",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (arrival.isRealTime) "GPS Live" else "Horario",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (arrival.minutesLeft <= 3) Color.White.copy(alpha = 0.85f) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

private fun getLineColor(lineNumber: String): Color {
    return when (lineNumber) {
        "110", "111", "343" -> Color(0xFF008751) // Exprés Sur
        "014", "015", "905" -> Color(0xFF0277BD) // Metro
        "102", "103", "101", "363" -> Color(0xFFE65100) // Norte
        "342", "348" -> Color(0xFF6A1B9A) // Teide
        "711" -> Color(0xFF263238) // Nocturna
        else -> Color(0xFF008751)
    }
}
