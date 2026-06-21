package com.magics.puffer.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magics.puffer.data.repository.SmokingRepository
import com.magics.puffer.domain.model.Achievement
import com.magics.puffer.domain.usecase.CheckAchievementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val smokeFreeCount: Int = 0,
    val daysToNext: Int? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val repository: SmokingRepository,
    private val checkAchievementsUseCase: CheckAchievementsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSmokeFreeCount().collect { smokeFree ->
                val list      = checkAchievementsUseCase.execute(smokeFree)
                val daysLeft  = checkAchievementsUseCase.daysUntilNext(smokeFree)
                _uiState.value = AchievementsUiState(
                    achievements   = list,
                    smokeFreeCount = smokeFree,
                    daysToNext     = daysLeft,
                    isLoading      = false
                )
            }
        }
    }
}
