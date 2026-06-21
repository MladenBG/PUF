package com.magics.puffer.data.repository

import com.magics.puffer.data.db.DailyLog
import com.magics.puffer.data.db.DailyLogDao
import com.magics.puffer.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for all smoking-related data.
 * ViewModels should ONLY interact with this repository, never directly with DAO or DataStore.
 */
@Singleton
class SmokingRepository @Inject constructor(
    private val dao: DailyLogDao,
    val prefs: UserPreferences
) {
    // ── Daily log operations ──────────────────────────────────────────────────

    fun getTodayLog(date: String): Flow<DailyLog?> = dao.getLogByDate(date)

    fun getAllLogs(): Flow<List<DailyLog>> = dao.getAllLogs()

    fun getLogsSince(fromDate: String): Flow<List<DailyLog>> = dao.getLogsSince(fromDate)

    fun getSmokeFreeCount(): Flow<Int> = dao.getSmokeFreeCount()

    fun getTotalSmoked(): Flow<Int?> = dao.getTotalSmoked()

    fun getStreakSince(fromDate: String): Flow<Int> = dao.getStreakSince(fromDate)

    suspend fun upsertLog(log: DailyLog) = dao.insertOrUpdate(log)

    /** Increment cigarette count for a given date by 1 */
    suspend fun addCigarette(date: String, currentLog: DailyLog?, targetForDay: Int) {
        val existing = currentLog ?: DailyLog(
            date = date,
            cigarettesSmoked = 0,
            targetForDay = targetForDay
        )
        dao.insertOrUpdate(existing.copy(cigarettesSmoked = existing.cigarettesSmoked + 1))
    }

    /** Decrement cigarette count (minimum 0) */
    suspend fun removeCigarette(date: String, currentLog: DailyLog?, targetForDay: Int) {
        val existing = currentLog ?: return
        val newCount = (existing.cigarettesSmoked - 1).coerceAtLeast(0)
        dao.insertOrUpdate(existing.copy(cigarettesSmoked = newCount))
    }

    // ── Preferences passthrough ───────────────────────────────────────────────
    val isOnboardingDone   = prefs.isOnboardingDone
    val userName           = prefs.userName
    val cigarettesPerDay   = prefs.cigarettesPerDay
    val pricePerPack       = prefs.pricePerPack
    val cigarettesPerPack  = prefs.cigarettesPerPack
    val startDate          = prefs.startDate
    val planDurationDays   = prefs.planDurationDays
    val currency           = prefs.currency
    val isSeedModeUnlocked = prefs.isSeedModeUnlocked
    val seedModeStart      = prefs.seedModeStart

    suspend fun saveOnboardingData(
        name: String, cigarettesPerDay: Int, pricePerPack: Float,
        cigarettesPerPack: Int, startDate: String, planDurationDays: Int, currency: String
    ) = prefs.saveOnboardingData(name, cigarettesPerDay, pricePerPack,
        cigarettesPerPack, startDate, planDurationDays, currency)

    suspend fun unlockSeedMode(startDate: String) = prefs.unlockSeedMode(startDate)

    suspend fun clearAll() = prefs.clearAll()
}
