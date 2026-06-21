package com.magics.puffer.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.magics.puffer.data.repository.SmokingRepository
import com.magics.puffer.ui.achievements.AchievementsScreen
import com.magics.puffer.ui.components.PufferBottomNav
import com.magics.puffer.ui.components.PufferScreen
import com.magics.puffer.ui.dashboard.DashboardScreen
import com.magics.puffer.ui.onboarding.OnboardingScreen
import com.magics.puffer.ui.plan.PlanScreen
import com.magics.puffer.ui.seeds.SeedModeScreen
import com.magics.puffer.ui.stats.StatsScreen
import com.magics.puffer.ui.theme.PufferDeepNavy
import com.magics.puffer.ui.theme.PufferViolet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Root ViewModel to determine start destination ──────────────────────────
data class RootUiState(
    val isOnboardingDone: Boolean = false,
    val isSeedUnlocked: Boolean = false,
    val isReady: Boolean = false   // true once prefs have been read at least once
)

@HiltViewModel
class RootViewModel @Inject constructor(
    private val repository: SmokingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RootUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.isOnboardingDone,
                repository.isSeedModeUnlocked
            ) { done, seed ->
                RootUiState(isOnboardingDone = done, isSeedUnlocked = seed, isReady = true)
            }.collect { _state.value = it }
        }
    }
}

/**
 * Root navigation host for Puffer.
 * Uses a ViewModel to determine the start destination from DataStore,
 * showing a loading spinner until preferences are read.
 */
@Composable
fun PufferNavHost(
    navController: NavHostController = rememberNavController(),
    rootViewModel: RootViewModel = hiltViewModel()
) {
    val rootState by rootViewModel.state.collectAsStateWithLifecycle()

    // Show spinner while DataStore prefs are loading
    if (!rootState.isReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PufferViolet)
        }
        return
    }

    val startDestination = if (rootState.isOnboardingDone) PufferScreen.Dashboard.route else "onboarding"
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: startDestination
    val showBottomBar = currentRoute != "onboarding"

    Scaffold(
        containerColor = PufferDeepNavy,
        bottomBar = {
            if (showBottomBar) {
                PufferBottomNav(
                    currentRoute = currentRoute,
                    onNavigate   = { route ->
                        navController.navigate(route) {
                            popUpTo(PufferScreen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    seedUnlocked = rootState.isSeedUnlocked
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = { fadeIn(tween(200)) },
            exitTransition   = { fadeOut(tween(150)) }
        ) {
            composable("onboarding") {
                OnboardingScreen(onFinished = {
                    navController.navigate(PufferScreen.Dashboard.route) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }

            composable(PufferScreen.Dashboard.route)    { DashboardScreen() }
            composable(PufferScreen.Plan.route)         { PlanScreen() }
            composable(PufferScreen.Stats.route)        { StatsScreen() }
            composable(PufferScreen.Achievements.route) { AchievementsScreen() }
            composable(PufferScreen.SeedMode.route)     { SeedModeScreen() }
        }
    }
}
