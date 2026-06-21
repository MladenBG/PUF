package com.magics.puffer.ui.plan

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magics.puffer.data.db.DailyLog
import com.magics.puffer.domain.model.DayPlan
import com.magics.puffer.domain.model.QuitPhase
import com.magics.puffer.ui.theme.*

/**
 * Plan screen — scrollable calendar showing every day of the quit journey.
 * Color coded: green (success), orange (over plan), red (missed), blue (today), gray (future).
 */
@Composable
fun PlanScreen(viewModel: PlanViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-scroll to today's entry
    val listState = rememberLazyListState()
    val todayIndex = remember(state.plan, state.todayStr) {
        state.plan.indexOfFirst { it.date == state.todayStr }.coerceAtLeast(0)
    }
    LaunchedEffect(todayIndex) {
        if (state.plan.isNotEmpty()) {
            listState.animateScrollToItem(index = (todayIndex - 2).coerceAtLeast(0))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PufferDeepNavy)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(PufferDarkCard, PufferDeepNavy))
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "Your Journey 📅",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    "${state.plan.size} day plan — one step at a time",
                    style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray)
                )
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendDot(PufferGreen, "Met goal")
            LegendDot(PufferOrange, "Slightly over")
            LegendDot(PufferRed, "Missed")
            LegendDot(PufferViolet, "Today")
            LegendDot(PufferBorderGray, "Upcoming")
        }

        HorizontalDivider(color = PufferBorderGray, modifier = Modifier.padding(horizontal = 16.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PufferViolet)
            }
            return@Column
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(state.plan) { _, dayPlan ->
                val log = state.logs[dayPlan.date]
                val isToday = dayPlan.date == state.todayStr
                val isPast = dayPlan.date < state.todayStr

                PlanDayRow(
                    dayPlan = dayPlan,
                    log     = log,
                    isToday = isToday,
                    isPast  = isPast
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = PufferGray))
    }
}

@Composable
private fun PlanDayRow(
    dayPlan: DayPlan,
    log: DailyLog?,
    isToday: Boolean,
    isPast: Boolean
) {
    val smoked = log?.cigarettesSmoked
    val target = dayPlan.targetCigarettes

    // Determine status color
    val statusColor = when {
        isToday             -> PufferViolet
        !isPast             -> PufferBorderGray     // future
        smoked == null      -> PufferBorderGray     // no log yet
        smoked == 0 && target == 0 -> PufferGreen   // zero-target day, stayed zero
        smoked == 0         -> PufferGreen           // logged zero
        smoked <= target    -> PufferGreen           // met or under
        smoked <= target + 2 -> PufferOrange         // slightly over
        else                -> PufferRed             // missed badly
    }

    val statusIcon = when {
        isToday          -> "▶"
        !isPast          -> "·"
        smoked == null   -> "?"
        smoked <= target -> "✓"
        else             -> "✗"
    }

    val isMilestone = dayPlan.dayNumber in listOf(7, 14, 30, 60, 90, 180, 365)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isToday    -> PufferViolet.copy(alpha = 0.12f)
                    isMilestone && isPast -> PufferGold.copy(alpha = 0.06f)
                    else       -> PufferDarkCard.copy(alpha = if (isPast) 1f else 0.5f)
                }
            )
            .border(
                width = if (isToday) 1.5.dp else if (isMilestone) 1.dp else 0.dp,
                color = if (isToday) PufferViolet else if (isMilestone) PufferGold.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Status circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.2f))
            ) {
                Text(statusIcon, fontSize = 14.sp, color = statusColor, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))

            // Day number + date
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Day ${dayPlan.dayNumber}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isToday) PufferWhite else if (isPast) PufferWhite else PufferGray
                        )
                    )
                    if (isMilestone) {
                        Text("⭐", fontSize = 12.sp)
                    }
                    if (dayPlan.phase == QuitPhase.ZERO) {
                        Text(
                            "ZERO",
                            style = MaterialTheme.typography.labelSmall.copy(color = PufferGreen, fontWeight = FontWeight.Bold),
                            fontSize = 10.sp
                        )
                    }
                }
                Text(
                    dayPlan.date,
                    style = MaterialTheme.typography.labelSmall.copy(color = PufferGray),
                    fontSize = 10.sp
                )
            }

            // Target
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (target == 0) "🚫" else "🎯 $target",
                    style = MaterialTheme.typography.labelMedium.copy(color = PufferGray)
                )
                Text("target", style = MaterialTheme.typography.labelSmall.copy(color = PufferGray, fontSize = 9.sp))
            }

            Spacer(Modifier.width(12.dp))

            // Actual smoked
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (smoked != null) "🚬 $smoked" else "—",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (smoked != null && smoked <= target) PufferGreen else if (smoked != null) PufferRed else PufferGray
                    )
                )
                Text("actual", style = MaterialTheme.typography.labelSmall.copy(color = PufferGray, fontSize = 9.sp))
            }
        }
    }
}
