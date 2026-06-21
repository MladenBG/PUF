package com.magics.puffer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing one day's smoking log.
 * Stores how many cigarettes the user smoked on a given date.
 */
@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey
    val date: String,           // Format: "yyyy-MM-dd"
    val cigarettesSmoked: Int,  // Number of cigarettes logged that day
    val targetForDay: Int,      // Planned target from the quit plan
    val noteText: String = "",  // Optional user note / mood entry
    val seedsUsed: Boolean = false // Whether user used seeds instead of smoking
)
