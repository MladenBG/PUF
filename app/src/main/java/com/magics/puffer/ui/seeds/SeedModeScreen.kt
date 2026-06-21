package com.magics.puffer.ui.seeds

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magics.puffer.ui.theme.*

/**
 * Seed Mode screen — unlocked after 90 days smoke-free.
 * Motivates users to use sunflower seeds as a habit replacement for the next 6 months.
 * No medical claims — purely motivational support for breaking the oral fixation habit.
 */
@Composable
fun SeedModeScreen(viewModel: SeedModeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Breathing animation for the sunflower emoji
    val infiniteTransition = rememberInfiniteTransition(label = "seed_anim")
    val scale by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 1.12f,
        animationSpec  = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label          = "seed_scale"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue   = -4f,
        targetValue    = 4f,
        animationSpec  = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label          = "seed_rotate"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PufferDeepNavy, Color(0xFF1A1205), PufferDeepNavy)
                )
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        // ── Sunflower hero ────────────────────────────────────────────────────
        Text(
            text = "🌻",
            fontSize = 96.sp,
            modifier = Modifier
                .scale(scale)
                .rotate(rotation)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Seed Mode 🌻",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = PufferGold
            )
        )

        if (!state.isUnlocked) {
            // Locked state
            Spacer(Modifier.height(12.dp))
            Text(
                text = "🔒 Unlocks after 90 smoke-free days",
                style = MaterialTheme.typography.bodyLarge.copy(color = PufferGray),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PufferDarkCard)
                    .border(1.dp, PufferBorderGray, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Keep logging zero cigarettes every day. After 90 consecutive smoke-free days, Seed Mode will automatically unlock — your personal 6-month seed challenge begins!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray),
                    textAlign = TextAlign.Center
                )
            }
            return@Column
        }

        // ── Unlocked content ──────────────────────────────────────────────────
        Spacer(Modifier.height(8.dp))
        Text(
            text = "You made it to the next chapter!",
            style = MaterialTheme.typography.titleMedium.copy(color = PufferGray),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Progress ring (6-month challenge)
        SeedProgressCard(
            daysIn       = state.seedDaysCount,
            daysRemaining= state.daysRemaining,
            progress     = state.progressFraction,
            modifier     = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Seed tips
        SeedTipsCard(modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(Modifier.height(20.dp))

        // Weekly seed milestones
        SeedMilestonesCard(
            daysIn   = state.seedDaysCount,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(32.dp))
    }
}

// ── Seed Progress Card ─────────────────────────────────────────────────────
@Composable
private fun SeedProgressCard(
    daysIn: Int,
    daysRemaining: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(PufferGoldGlow, PufferDarkCard))
            )
            .border(
                1.5.dp,
                Brush.linearGradient(listOf(PufferGold, PufferGoldLight)),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                "6-Month Seed Challenge",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = PufferGold
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Day $daysIn of 180",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = PufferWhite
                )
            )
            if (daysRemaining > 0) {
                Text(
                    "$daysRemaining days remaining",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray)
                )
            } else {
                Text(
                    "🎉 Challenge Complete!",
                    style = MaterialTheme.typography.titleMedium.copy(color = PufferGold, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress   = { progress },
                modifier   = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color      = PufferGold,
                trackColor = PufferBorderGray
            )
        }
    }
}

// ── Seed Tips ─────────────────────────────────────────────────────────────
@Composable
private fun SeedTipsCard(modifier: Modifier = Modifier) {
    val tips = listOf(
        "🌻" to "Keep a small bag of sunflower seeds in your pocket or bag — swap for a cigarette when the urge hits.",
        "👄" to "The cracking and chewing action satisfies the oral habit that smoking creates.",
        "⏱️" to "A craving lasts about 3–5 minutes. A handful of seeds takes exactly that long!",
        "💧" to "Drink water alongside seeds — it keeps your hands and mouth busy.",
        "🌰" to "Try different flavors: salted, spicy, BBQ — keep it interesting!",
        "🤲" to "Splitting seeds also keeps your hands busy, which helps with restlessness."
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PufferDarkCard)
            .border(1.dp, PufferGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Seed Strategy Tips",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PufferGold)
            )
            tips.forEach { (emoji, tip) ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(emoji, fontSize = 18.sp, modifier = Modifier.padding(top = 2.dp))
                    Text(tip, style = MaterialTheme.typography.bodySmall.copy(color = PufferWhite))
                }
            }
        }
    }
}

// ── Seed Milestones ───────────────────────────────────────────────────────
@Composable
private fun SeedMilestonesCard(daysIn: Int, modifier: Modifier = Modifier) {
    val milestones = listOf(
        7   to "1 week — the habit is forming!",
        14  to "2 weeks — you're building a new routine",
        30  to "1 month — seeds are your new best friend 🌻",
        60  to "2 months — the old habit is fading fast",
        90  to "3 months — halfway through, still going strong!",
        120 to "4 months — the new you is fully here",
        150 to "5 months — almost there, keep it up!",
        180 to "6 months complete — YOU ARE OFFICIALLY FREE 🎉"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PufferDarkCard)
            .border(1.dp, PufferBorderGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Seed Milestones",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            milestones.forEach { (days, label) ->
                val reached = daysIn >= days
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        if (reached) "✅" else "⬜",
                        fontSize = 16.sp
                    )
                    Text(
                        "Day $days — $label",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color      = if (reached) PufferGold else PufferGray,
                            fontWeight = if (reached) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}
