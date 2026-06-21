package com.magics.puffer.ui.achievements

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magics.puffer.ui.components.AchievementBadge
import com.magics.puffer.ui.theme.*

/**
 * Achievements screen — all milestone badges in a list.
 * Shows progress toward the next badge with a motivational progress bar.
 */
@Composable
fun AchievementsScreen(viewModel: AchievementsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val unlockedCount = state.achievements.count { it.isUnlocked }
    val total         = state.achievements.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PufferDeepNavy)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(PufferDarkCard, PufferDeepNavy)))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "Your Badges 🏆",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$unlockedCount of $total earned — keep going!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray)
                )
                Spacer(Modifier.height(12.dp))

                // Overall progress bar
                LinearProgressIndicator(
                    progress = { if (total > 0) unlockedCount.toFloat() / total else 0f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color    = PufferViolet,
                    trackColor = PufferBorderGray
                )

                state.daysToNext?.let { days ->
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "🎯 Next badge in $days smoke-free day${if (days != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium.copy(color = PufferTeal, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PufferViolet)
            }
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.achievements) { achievement ->
                AchievementBadge(achievement = achievement, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
