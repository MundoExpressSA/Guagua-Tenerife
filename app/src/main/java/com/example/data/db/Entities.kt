package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stops")
data class FavoriteStopEntity(
    @PrimaryKey val stopCode: String,
    val stopName: String,
    val municipality: String,
    val linesJoined: String,
    val latitude: Double,
    val longitude: Double,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "delay_alerts")
data class DelayAlertEntity(
    @PrimaryKey val lineCode: String,
    val lineName: String,
    val alertThresholdMinutes: Int = 3,
    val isEnabled: Boolean = true,
    val lastNotifiedTimestamp: Long = 0L
)

@Entity(tableName = "saved_lines")
data class SavedLineEntity(
    @PrimaryKey val lineNumber: String,
    val originDestination: String,
    val category: String,
    val colorHex: String
)
