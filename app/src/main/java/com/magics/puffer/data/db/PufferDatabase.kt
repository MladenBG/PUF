package com.magics.puffer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for Puffer.
 * Single-table design: daily_logs tracks per-day cigarette counts.
 */
@Database(
    entities = [DailyLog::class],
    version = 1,
    exportSchema = false
)
abstract class PufferDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        const val DATABASE_NAME = "puffer_db"
    }
}
