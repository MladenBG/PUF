package com.magics.puffer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Extension to create the DataStore instance at the Context level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "puffer_prefs")

/**
 * DataStore wrapper for user preferences and quit plan settings.
 * All data is stored locally — no network calls, no data sharing.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ── Keys ─────────────────────────────────────────────────────────────────
    companion object {
        val KEY_ONBOARDING_DONE     = booleanPreferencesKey("onboarding_done")
        val KEY_USER_NAME           = stringPreferencesKey("user_name")
        val KEY_CIGARETTES_PER_DAY  = intPreferencesKey("cigarettes_per_day")
        val KEY_PRICE_PER_PACK      = floatPreferencesKey("price_per_pack")
        val KEY_CIGARETTES_PER_PACK = intPreferencesKey("cigarettes_per_pack")
        val KEY_START_DATE          = stringPreferencesKey("start_date")       // yyyy-MM-dd
        val KEY_PLAN_DURATION_DAYS  = intPreferencesKey("plan_duration_days")  // e.g. 30, 60, 90
        val KEY_CURRENCY            = stringPreferencesKey("currency")          // "RSD", "EUR", "USD"
        val KEY_SEED_MODE_UNLOCKED  = booleanPreferencesKey("seed_mode_unlocked")
        val KEY_SEED_MODE_START     = stringPreferencesKey("seed_mode_start")   // yyyy-MM-dd
    }

    // ── Read flows ────────────────────────────────────────────────────────────
    val isOnboardingDone: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_ONBOARDING_DONE] ?: false }

    val userName: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_USER_NAME] ?: "" }

    val cigarettesPerDay: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_CIGARETTES_PER_DAY] ?: 20 }

    val pricePerPack: Flow<Float> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_PRICE_PER_PACK] ?: 300f }  // default 300 RSD

    val cigarettesPerPack: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_CIGARETTES_PER_PACK] ?: 20 }

    val startDate: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_START_DATE] ?: "" }

    val planDurationDays: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_PLAN_DURATION_DAYS] ?: 90 }

    val currency: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_CURRENCY] ?: "RSD" }

    val isSeedModeUnlocked: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_SEED_MODE_UNLOCKED] ?: false }

    val seedModeStart: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_SEED_MODE_START] ?: "" }

    // ── Write functions ───────────────────────────────────────────────────────
    suspend fun saveOnboardingData(
        name: String,
        cigarettesPerDay: Int,
        pricePerPack: Float,
        cigarettesPerPack: Int,
        startDate: String,
        planDurationDays: Int,
        currency: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_DONE]     = true
            prefs[KEY_USER_NAME]           = name
            prefs[KEY_CIGARETTES_PER_DAY]  = cigarettesPerDay
            prefs[KEY_PRICE_PER_PACK]      = pricePerPack
            prefs[KEY_CIGARETTES_PER_PACK] = cigarettesPerPack
            prefs[KEY_START_DATE]          = startDate
            prefs[KEY_PLAN_DURATION_DAYS]  = planDurationDays
            prefs[KEY_CURRENCY]            = currency
        }
    }

    suspend fun unlockSeedMode(startDate: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SEED_MODE_UNLOCKED] = true
            prefs[KEY_SEED_MODE_START]    = startDate
        }
    }

    /** Reset everything — used by "Start over" option */
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
