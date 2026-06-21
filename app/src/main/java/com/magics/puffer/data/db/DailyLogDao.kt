package com.magics.puffer.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for daily smoking logs.
 * All queries return Flow for reactive UI updates.
 */
@Dao
interface DailyLogDao {

    /** Insert or replace a log entry for a given date */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DailyLog)

    /** Get a single day's log by date string (yyyy-MM-dd) */
    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    fun getLogByDate(date: String): Flow<DailyLog?>

    /** Get all logs ordered by date ascending — used for charts and plan view */
    @Query("SELECT * FROM daily_logs ORDER BY date ASC")
    fun getAllLogs(): Flow<List<DailyLog>>

    /** Get logs from a start date onward — useful for streak calculation */
    @Query("SELECT * FROM daily_logs WHERE date >= :fromDate ORDER BY date ASC")
    fun getLogsSince(fromDate: String): Flow<List<DailyLog>>

    /** Count total days where cigarettes smoked was 0 */
    @Query("SELECT COUNT(*) FROM daily_logs WHERE cigarettesSmoked = 0")
    fun getSmokeFreeCount(): Flow<Int>

    /** Total cigarettes logged (for statistics: avoided = original habit * days - this total) */
    @Query("SELECT SUM(cigarettesSmoked) FROM daily_logs")
    fun getTotalSmoked(): Flow<Int?>

    /** Count consecutive smoke-free days up to today (streak) */
    @Query("SELECT COUNT(*) FROM daily_logs WHERE cigarettesSmoked = 0 AND date >= :fromDate")
    fun getStreakSince(fromDate: String): Flow<Int>

    /** Delete a log entry by date */
    @Query("DELETE FROM daily_logs WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
