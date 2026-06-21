package com.magics.puffer.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magics.puffer.data.repository.SmokingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

data class OnboardingUiState(
    val name: String = "",
    val cigarettesPerDay: Int = 20,
    val pricePerPack: Float = 300f,
    val cigarettesPerPack: Int = 20,
    val planDays: Int = 90,
    val currency: String = "RSD",
    val isSaving: Boolean = false,
    val isDone: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: SmokingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(value: String)     = _uiState.update { it.copy(name = value) }
    fun onCigsPerDayChange(value: Int)  = _uiState.update { it.copy(cigarettesPerDay = value.coerceIn(1, 100)) }
    fun onPriceChange(value: Float)     = _uiState.update { it.copy(pricePerPack = value.coerceAtLeast(0f)) }
    fun onCigsPerPackChange(value: Int) = _uiState.update { it.copy(cigarettesPerPack = value.coerceIn(1, 40)) }
    fun onPlanDaysChange(value: Int)    = _uiState.update { it.copy(planDays = value) }
    fun onCurrencyChange(value: String) = _uiState.update { it.copy(currency = value) }

    fun saveAndStart() {
        val state = _uiState.value
        if (state.name.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

            repository.saveOnboardingData(
                name              = state.name,
                cigarettesPerDay  = state.cigarettesPerDay,
                pricePerPack      = state.pricePerPack,
                cigarettesPerPack = state.cigarettesPerPack,
                startDate         = today,
                planDurationDays  = state.planDays,
                currency          = state.currency
            )
            _uiState.update { it.copy(isSaving = false, isDone = true) }
        }
    }
}
