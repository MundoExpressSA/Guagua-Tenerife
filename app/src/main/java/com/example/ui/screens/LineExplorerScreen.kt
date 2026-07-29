package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusLine
import com.example.data.model.LineCategory
import com.example.ui.theme.TitsaGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineExplorerScreen(
    lines: List<BusLine>,
    activeCategory: LineCategory?,
    savedLineNumbers: List<String>,
    onCategorySelect: (LineCategory?) -> Unit,
    onLineSelect: (BusLine) -> Unit,
    onSavedLineToggle: (BusLine, Boolean) -> Unit,
    onSimulateDelayClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLineForDetail by remember { mutableStateOf<BusLine?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Title Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Líneas de Guagua TITSA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Rutas por toda la isla de Tenerife",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = activeCategory == null,
                            onClick = { onCategorySelect(null) },
                            label = { Text("Todas (${lines.size})", fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("filter_category_all")
                        )
                    }

                    items(LineCategory.values()) { category ->
                        val isSelected = activeCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(if (isSelected) null else category) },
                            label = { Text(category.displayName, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("filter_category_${category.name}")
                        )
                    }
                }
            }
        }

        // List of Lines
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = lines,
                key = { it.lineNumber }
            ) { line ->
                val isSaved = savedLineNumbers.contains(line.lineNumber)

                ElevatedCard(
                    onClick = { selectedLineForDetail = line },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("line_card_${line.lineNumber}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(android.graphics.Color.parseColor(line.colorHex)),
                            contentColor = Color.White
                        ) {
                            Box(
                                modifier = Modifier.size(50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = line.lineNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Línea ${line.lineNumber}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (line.isExpress) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = TitsaGreenPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = "DIRECTO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${line.origin} ➔ ${line.destination}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Frecuencia cada ${line.frequencyMinutesPeak} min • ${line.operatingHours}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        IconButton(
                            onClick = { onSavedLineToggle(line, isSaved) },
                            modifier = Modifier.testTag("bookmark_line_${line.lineNumber}")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Guardar",
                                tint = if (isSaved) TitsaGreenPrimary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Detailed Line Information
    selectedLineForDetail?.let { line ->
        val isSaved = savedLineNumbers.contains(line.lineNumber)
        AlertDialog(
            onDismissRequest = { selectedLineForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(android.graphics.Color.parseColor(line.colorHex)),
                        contentColor = Color.White
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(line.lineNumber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Línea ${line.lineNumber}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(line.category.displayName, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ruta: ${line.origin} ➔ ${line.destination}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Horario de servicio: ${line.operatingHours}",
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Frecuencia en hora punta: Cada ${line.frequencyMinutesPeak} minutos",
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            onSimulateDelayClick(line.lineNumber)
                            selectedLineForDetail = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Probar Alerta de Retraso de Línea")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSavedLineToggle(line, isSaved)
                    selectedLineForDetail = null
                }) {
                    Text(if (isSaved) "Quitar de Guardadas" else "Guardar Línea")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedLineForDetail = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
