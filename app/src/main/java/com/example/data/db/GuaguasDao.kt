package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GuaguasDao {
    // Favorite Stops
    @Query("SELECT * FROM favorite_stops ORDER BY addedTimestamp DESC")
    fun getAllFavoriteStops(): Flow<List<FavoriteStopEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stops WHERE stopCode = :code)")
    fun isStopFavorite(code: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteStop(stop: FavoriteStopEntity)

    @Query("DELETE FROM favorite_stops WHERE stopCode = :code")
    suspend fun deleteFavoriteStop(code: String)

    // Delay Alerts
    @Query("SELECT * FROM delay_alerts")
    fun getAllDelayAlerts(): Flow<List<DelayAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelayAlert(alert: DelayAlertEntity)

    @Query("DELETE FROM delay_alerts WHERE lineCode = :lineCode")
    suspend fun deleteDelayAlert(lineCode: String)

    // Saved Lines
    @Query("SELECT * FROM saved_lines")
    fun getAllSavedLines(): Flow<List<SavedLineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedLine(line: SavedLineEntity)

    @Query("DELETE FROM saved_lines WHERE lineNumber = :lineNumber")
    suspend fun deleteSavedLine(lineNumber: String)
}
