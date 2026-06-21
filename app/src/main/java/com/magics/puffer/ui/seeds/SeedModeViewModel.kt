package com.magics.puffer.ui.seeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magics.puffer.data.repository.SmokingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

data class SeedModeUiState(
    val isUnlocked: Boolean = false,
    val startDate: String = "",
    val seedDaysCount: Int = 0,  // Days since seed mode began
    val daysRemaining: Int = 180,
    val progressFraction: Float = 0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class SeedModeViewModel @Inject constructor(
    private val repository: SmokingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeedModeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.isSeedModeUnlocked,
                repository.seedModeStart
            ) { unlocked, seedStart ->
                if (!unlocked || seedStart.isBlank()) {
                    SeedModeUiState(isUnlocked = false, isLoading = false)
                } else {
                    val today = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val start = LocalDate.parse(seedStart)
                    val daysSince = (today.toEpochDays() - start.toEpochDays()).toInt().coerceAtLeast(0)
                    val remaining = (180 - daysSince).coerceAtLeast(0)
                    val progress  = (daysSince.toFloat() / 180f).coerceIn(0f, 1f)

                    SeedModeUiState(
                        isUnlocked       = true,
                        startDate        = seedStart,
                        seedDaysCount    = daysSince,
                        daysRemaining    = remaining,
                        progressFraction = progress,
                        isLoading        = false
                    )
                }
            }.catch { }.collect { _uiState.value = it }
        }
    }
}
