package com.magics.puffer.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magics.puffer.domain.model.Achievement
import com.magics.puffer.ui.components.CircularProgressRing
import com.magics.puffer.ui.components.DailyTipCard
import com.magics.puffer.ui.theme.*
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Dashboard (Home) screen — the main screen of Puffer.
 * Shows today's progress ring, + / - cigarette buttons,
 * streak, money saved, and today's motivational tip.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Show achievement popup when a new one unlocks
    state.newlyUnlocked?.let { achievement ->
        AchievementUnlockedDialog(
            achievement = achievement,
            onDismiss = viewModel::dismissAchievement
        )
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PufferViolet)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PufferDeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                val greeting = when (state.todayStr.takeLast(2).toIntOrNull() ?: 0) {
                    in 0..11 -> "Good morning"
                    in 12..17 -> "Good afternoon"
                    else -> "Good evening"
                }
                Text(
                    text = if (state.userName.isNotBlank()) "$greeting, ${state.userName}!" else "$greeting!",
                    style = MaterialTheme.typography.titleMedium.copy(color = PufferGray)
                )
                Text(
                    text = "Day ${state.dayNumber} of your journey",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            // Flame streak badge
            StreakBadge(state.smokeFreeStreak)
        }

        Spacer(Modifier.height(32.dp))

        // ── Progress ring ────────────────────────────────────────────────────
        val smoked = state.todayLog?.cigarettesSmoked ?: 0
        CircularProgressRing(
            smoked = smoked,
            target = state.todayTarget,
            size   = 230.dp
        )

        Spacer(Modifier.height(8.dp))

        // Status message
        val statusMsg = when {
            state.todayTarget == 0        -> "🎉 Target: Zero today — stay strong!"
            smoked == 0                   -> "✅ None yet — amazing start!"
            smoked < state.todayTarget    -> "👍 On track — ${state.todayTarget - smoked} left in your plan"
            smoked == state.todayTarget   -> "⚠️ At today's limit"
            else                          -> "❌ Over plan by ${smoked - state.todayTarget} — tomorrow is a new day"
        }
        Text(
            text  = statusMsg,
            style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        // ── + / - buttons ────────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Remove button
            CigButton(
                label = "−",
                color = PufferBorderGray,
                enabled = smoked > 0,
                onClick = viewModel::removeCigarette
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🚬", fontSize = 32.sp)
                Text(
                    "Log a cigarette",
                    style = MaterialTheme.typography.labelMedium.copy(color = PufferGray)
                )
            }

            // Add button
            CigButton(
                label = "+",
                color = PufferViolet,
                enabled = true,
                onClick = viewModel::addCigarette
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Stats row ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier  = Modifier.weight(1f),
                icon      = "💰",
                label     = "Saved",
                value     = "${String.format(Locale.US, "%.0f", state.moneySaved)} ${state.currency}"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon     = "🚫",
                label    = "Avoided",
                value    = "${state.cigarettesAvoided} cigs"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon     = "📅",
                label    = "Free days",
                value    = "${state.smokeFreeStreak}"
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Daily tip ────────────────────────────────────────────────────────
        DailyTipCard(dayNumber = state.dayNumber, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(32.dp))
    }
}

// ── Streak Badge ──────────────────────────────────────────────────────────
@Composable
private fun StreakBadge(streak: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(listOf(PufferViolet.copy(alpha = 0.25f), PufferTeal.copy(alpha = 0.15f)))
            )
            .border(1.dp, PufferViolet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("🔥", fontSize = 18.sp)
            Text(
                text  = "$streak",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = PufferWhite
                )
            )
        }
    }
}

// ── Stat Card ─────────────────────────────────────────────────────────────
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PufferDarkCard)
            .border(1.dp, PufferBorderGray, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = PufferGray))
    }
}

// ── Cig +/- Button ────────────────────────────────────────────────────────
@Composable
private fun CigButton(
    label: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cig_button_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .scale(scale)
            .size(64.dp)
            .clip(CircleShape)
            .background(if (enabled) color else PufferBorderGray)
            .clickable(enabled = enabled) {
                pressed = true
                onClick()
            }
    ) {
        Text(label, fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(120)
            pressed = false
        }
    }
}

// ── Achievement Unlock Dialog ──────────────────────────────────────────────
@Composable
private fun AchievementUnlockedDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(listOf(PufferDarkCard, PufferDeepNavy))
                )
                .border(
                    1.5.dp,
                    Brush.linearGradient(listOf(PufferViolet, PufferTeal)),
                    RoundedCornerShape(24.dp)
                )
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(achievement.emoji, fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Achievement Unlocked!",
                style = MaterialTheme.typography.labelLarge.copy(color = PufferTeal)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                achievement.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                achievement.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PufferViolet)
            ) {
                Text("Awesome! 🎉", fontWeight = FontWeight.Bold)
            }
        }
    }
}
