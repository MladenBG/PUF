package com.magics.puffer.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magics.puffer.data.db.DailyLog
import com.magics.puffer.data.repository.SmokingRepository
import com.magics.puffer.domain.model.DayPlan
import com.magics.puffer.domain.usecase.GenerateQuitPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

data class PlanUiState(
    val plan: List<DayPlan> = emptyList(),
    val logs: Map<String, DailyLog> = emptyMap(),
    val todayStr: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: SmokingRepository,
    private val generatePlanUseCase: GenerateQuitPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPlan()
    }

    private fun loadPlan() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

            combine(
                repository.startDate,
                repository.cigarettesPerDay,
                repository.planDurationDays,
                repository.getAllLogs()
            ) { startDate, cigsPerDay, planDays, logs ->
                val plan = generatePlanUseCase.execute(startDate, cigsPerDay, planDays)
                val logsMap = logs.associateBy { it.date }

                PlanUiState(
                    plan      = plan,
                    logs      = logsMap,
                    todayStr  = today,
                    isLoading = false
                )
            }.catch { }.collect { _uiState.value = it }
        }
    }
}
