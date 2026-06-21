package com.magics.puffer.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magics.puffer.data.db.DailyLog
import com.magics.puffer.data.repository.SmokingRepository
import com.magics.puffer.domain.model.Achievement
import com.magics.puffer.domain.usecase.CheckAchievementsUseCase
import com.magics.puffer.domain.usecase.GenerateQuitPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "",
    val todayStr: String = "",
    val dayNumber: Int = 1,
    val todayLog: DailyLog? = null,
    val todayTarget: Int = 20,
    val smokeFreeStreak: Int = 0,
    val moneySaved: Float = 0f,
    val currency: String = "RSD",
    val cigarettesAvoided: Int = 0,
    val achievements: List<Achievement> = emptyList(),
    val newlyUnlocked: Achievement? = null,
    val seedModeUnlocked: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: SmokingRepository,
    private val generatePlanUseCase: GenerateQuitPlanUseCase,
    private val checkAchievementsUseCase: CheckAchievementsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    // Track which achievements were already shown to avoid repeated popups
    private val shownAchievements = mutableSetOf<String>()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val todayStr = today.toString()

            // Combine all required flows
            combine(
                repository.userName,
                repository.cigarettesPerDay,
                repository.pricePerPack,
                repository.cigarettesPerPack,
                repository.startDate,
                repository.planDurationDays,
                repository.currency,
                repository.isSeedModeUnlocked,
                repository.getTodayLog(todayStr),
                repository.getSmokeFreeCount()
            ) { values ->
                val name        = values[0] as String
                val cigsPerDay  = values[1] as Int
                val pricePerPk  = values[2] as Float
                val cigsPerPk   = values[3] as Int
                val startDate   = values[4] as String
                val planDays    = values[5] as Int
                val currency    = values[6] as String
                val seedUnlocked= values[7] as Boolean
                val todayLog    = values[8] as? DailyLog
                val smokeFreeDays = values[9] as Int

                // Calculate today's target from the plan
                val plan = generatePlanUseCase.execute(startDate, cigsPerDay, planDays)
                val todayPlan = generatePlanUseCase.getTodayTarget(plan, todayStr)
                val target = todayPlan?.targetCigarettes ?: cigsPerDay

                // Day number since start
                val dayNum = if (startDate.isNotBlank()) {
                    val start = LocalDate.parse(startDate)
                    (today.toEpochDays() - start.toEpochDays() + 1).toInt().coerceAtLeast(1)
                } else 1

                // Money saved: (cigarettes avoided) × (price per cigarette)
                val pricePerCig = pricePerPk / cigsPerPk
                val originalTotal = dayNum * cigsPerDay
                val smoked = todayLog?.cigarettesSmoked ?: 0
                // We need total smoked from all logs, approximate with smoke-free days
                val avoided = (smokeFreeDays * cigsPerDay).coerceAtLeast(0)
                val saved = avoided * pricePerCig

                // Achievements
                val achievementList = checkAchievementsUseCase.execute(smokeFreeDays)


                // Find newly unlocked achievement for popup
                val newAch = achievementList
                    .filter { it.isUnlocked && it.id !in shownAchievements }
                    .maxByOrNull { it.requiredDays }

                newAch?.let { shownAchievements.add(it.id) }

                DashboardUiState(
                    userName          = name,
                    todayStr          = todayStr,
                    dayNumber         = dayNum,
                    todayLog          = todayLog,
                    todayTarget       = target,
                    smokeFreeStreak   = smokeFreeDays,
                    moneySaved        = saved,
                    currency          = currency,
                    cigarettesAvoided = avoided,
                    achievements      = achievementList,
                    newlyUnlocked     = newAch,
                    seedModeUnlocked  = seedUnlocked,
                    isLoading         = false
                )
            }.catch { /* handle gracefully */ }.collect { state ->
                _uiState.value = state
                // Unlock seed mode if eligible — suspend call must happen outside combine lambda
                if (checkAchievementsUseCase.isSeedModeEligible(state.smokeFreeStreak)
                    && !state.seedModeUnlocked) {
                    repository.unlockSeedMode(state.todayStr)
                }
            }
        }
    }

    fun addCigarette() {
        val state = _uiState.value
        viewModelScope.launch {
            repository.addCigarette(state.todayStr, state.todayLog, state.todayTarget)
        }
    }

    fun removeCigarette() {
        val state = _uiState.value
        viewModelScope.launch {
            repository.removeCigarette(state.todayStr, state.todayLog, state.todayTarget)
        }
    }

    fun dismissAchievement() {
        _uiState.update { it.copy(newlyUnlocked = null) }
    }
}
