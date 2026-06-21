package com.magics.puffer.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magics.puffer.data.db.DailyLog
import com.magics.puffer.data.repository.SmokingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

data class StatsUiState(
    val logs: List<DailyLog> = emptyList(),
    val totalCigsAvoided: Int = 0,
    val totalMoneySaved: Float = 0f,
    val smokeFreeCount: Int = 0,
    val currency: String = "RSD",
    val startDateStr: String = "",
    val originalPerDay: Int = 20,
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: SmokingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            combine(
                repository.getAllLogs(),
                repository.cigarettesPerDay,
                repository.pricePerPack,
                repository.cigarettesPerPack,
                repository.currency,
                repository.startDate,
                repository.getSmokeFreeCount()
            ) { values ->
                val logs        = values[0] as List<DailyLog>
                val cigsPerDay  = values[1] as Int
                val pricePerPk  = values[2] as Float
                val cigsPerPk   = values[3] as Int
                val currency    = values[4] as String
                val startDate   = values[5] as String
                val smokeFree   = values[6] as Int

                val pricePerCig = pricePerPk / cigsPerPk
                val daysElapsed = logs.size
                val totalPotential = daysElapsed * cigsPerDay
                val totalActual    = logs.sumOf { it.cigarettesSmoked }
                val avoided        = (totalPotential - totalActual).coerceAtLeast(0)
                val saved          = avoided * pricePerCig

                StatsUiState(
                    logs              = logs.takeLast(30), // Show last 30 days in chart
                    totalCigsAvoided  = avoided,
                    totalMoneySaved   = saved,
                    smokeFreeCount    = smokeFree,
                    currency          = currency,
                    startDateStr      = startDate,
                    originalPerDay    = cigsPerDay,
                    isLoading         = false
                )
            }.catch { }.collect { _uiState.value = it }
        }
    }
}
