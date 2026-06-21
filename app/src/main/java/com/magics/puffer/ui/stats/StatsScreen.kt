package com.magics.puffer.ui.stats

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.magics.puffer.ui.theme.*
import java.util.Locale

// Vico Charts 1.15.0 Stable Imports
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

/**
 * Stats screen — shows aggregate savings, cigarettes avoided, and the
 * general health timeline (motivational only, no medical language).
 */
@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PufferDeepNavy)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(PufferDarkCard, PufferDeepNavy)))
                .padding(20.dp)
        ) {
            Text(
                "Your Stats 📊",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PufferViolet)
            }
            return@Column
        }

        Spacer(Modifier.height(8.dp))

        // Big stats cards
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BigStatCard(
                modifier = Modifier.weight(1f),
                emoji = "💰",
                value = "${String.format(Locale.US, "%.0f", state.totalMoneySaved)}",
                unit  = state.currency,
                label = "Total Saved"
            )
            BigStatCard(
                modifier = Modifier.weight(1f),
                emoji = "🚫",
                value = "${state.totalCigsAvoided}",
                unit  = "cigs",
                label = "Not Smoked"
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BigStatCard(
                modifier = Modifier.weight(1f),
                emoji = "📅",
                value = "${state.smokeFreeCount}",
                unit  = "days",
                label = "Smoke-Free Days"
            )
            BigStatCard(
                modifier = Modifier.weight(1f),
                emoji = "📉",
                value = "${state.originalPerDay}→${(state.logs.lastOrNull()?.cigarettesSmoked ?: state.originalPerDay)}",
                unit  = "/day",
                label = "Cigs Reduction"
            )
        }

        Spacer(Modifier.height(20.dp))

        // Savings visual bar
        SavingsBar(
            moneySaved = state.totalMoneySaved,
            currency   = state.currency,
            modifier   = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Professional Vico Chart: last 30 days
        if (state.logs.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PufferDarkCard)
                    .border(1.dp, PufferBorderGray, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "Last 30 Days — Cigarettes per Day",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.height(24.dp))

                    // Convert database logs to Vico Chart entries safely
                    val chartEntries = remember(state.logs) {
                        state.logs.mapIndexed { index, log ->
                            FloatEntry(
                                x = index.toFloat(),
                                y = log.cigarettesSmoked.toFloat()
                            )
                        }
                    }

                    if (chartEntries.isNotEmpty()) {
                        val chartEntryModelProducer = remember(chartEntries) {
                            ChartEntryModelProducer(chartEntries)
                        }

                        Chart(
                            chart = columnChart(),
                            chartModelProducer = chartEntryModelProducer,
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        Text(
                            "Not enough data to display chart.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Track your daily progress and reductions.",
                        style = MaterialTheme.typography.labelSmall.copy(color = PufferGray),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // General benefit timeline
        BenefitTimeline(smokeFreeCount = state.smokeFreeCount, modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(Modifier.height(32.dp))
    }
}

// Big Stat Card
@Composable
private fun BigStatCard(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    unit: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PufferDarkCard)
            .border(1.dp, PufferBorderGray, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 28.sp)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = PufferViolet))
            Text(unit, style = MaterialTheme.typography.labelMedium.copy(color = PufferGray))
        }
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = PufferGray), textAlign = TextAlign.Center)
    }
}

// Savings Bar
@Composable
private fun SavingsBar(moneySaved: Float, currency: String, modifier: Modifier = Modifier) {
    val milestones = listOf(500f to "500", 1000f to "1K", 2000f to "2K", 5000f to "5K")
    val nextMilestone = milestones.firstOrNull { moneySaved < it.first } ?: (10000f to "10K")
    val progress = (moneySaved / nextMilestone.first).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PufferDarkCard)
            .border(1.dp, PufferBorderGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Savings Milestone 🎯",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(PufferBorderGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(7.dp))
                        .background(Brush.horizontalGradient(listOf(PufferViolet, PufferTeal)))
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${String.format(Locale.US, "%.0f", moneySaved)} $currency saved",
                    style = MaterialTheme.typography.labelMedium.copy(color = PufferTeal, fontWeight = FontWeight.Bold)
                )
                Text(
                    "Goal: ${nextMilestone.second} $currency",
                    style = MaterialTheme.typography.labelMedium.copy(color = PufferGray)
                )
            }
        }
    }
}

// Benefit Timeline
@Composable
private fun BenefitTimeline(smokeFreeCount: Int, modifier: Modifier = Modifier) {
    // General well-being benefits — purely motivational, no medical claims
    val benefits = listOf(
        Triple(0,   "💨", "Air quality around you improved from day one"),
        Triple(2,   "👅", "After 48 hours: food starts tasting better"),
        Triple(7,   "🏃", "After 1 week: stairs feel easier, breathing improves"),
        Triple(14,  "❤️", "After 2 weeks: circulation noticeably better"),
        Triple(30,  "💪", "After 1 month: energy levels significantly higher"),
        Triple(60,  "🌬️", "After 2 months: morning cough begins to ease"),
        Triple(90,  "🌻", "After 3 months: Seed Mode unlocked — you did it!"),
        Triple(180, "💎", "After 6 months: a completely new daily routine"),
        Triple(365, "👑", "After 1 year: your life is transformed. You are free!")
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PufferDarkCard)
            .border(1.dp, PufferBorderGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "General Well-Being Timeline",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                "Based on commonly reported personal experiences — not medical advice.",
                style = MaterialTheme.typography.labelSmall.copy(color = PufferGray, fontSize = 10.sp)
            )
            Spacer(Modifier.height(4.dp))

            benefits.forEach { (days, icon, text) ->
                val reached = smokeFreeCount >= days
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        icon,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Column {
                        Text(
                            text,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (reached) PufferWhite else PufferGray
                            )
                        )
                        if (reached && days > 0) {
                            Text(
                                "✓ Reached!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PufferGreen, fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}