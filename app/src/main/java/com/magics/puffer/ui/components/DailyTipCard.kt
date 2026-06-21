package com.magics.puffer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.magics.puffer.ui.theme.*

/**
 * Daily motivational tip card shown on the dashboard.
 * Uses an array of tips indexed by the current day number (modulo tip count).
 */
@Composable
fun DailyTipCard(
    dayNumber: Int,
    modifier: Modifier = Modifier
) {
    val tip = dailyTips[dayNumber % dailyTips.size]

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(PufferDarkCard, PufferDarkerCard)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(PufferViolet.copy(alpha = 0.4f), PufferTeal.copy(alpha = 0.4f))),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Tip",
                tint = PufferGold,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text  = "Tip of the Day",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PufferGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = tip,
                    style = MaterialTheme.typography.bodyMedium.copy(color = PufferWhite)
                )
            }
        }
    }
}

/**
 * 30-day rotating tip bank — motivational only, no medical advice.
 * Language: habit, savings, motivation, fitness, support.
 */
private val dailyTips = listOf(
    "Start today! Every day without a cigarette is money saved and a habit broken.",
    "Keep a glass of water nearby. Sipping water can help you push through a craving.",
    "Your taste buds are waking up! Food starts tasting better after just a few days.",
    "Replace the hand-to-mouth habit with sunflower seeds, gum, or a stress ball.",
    "Count your savings — open a jar and put in the money you didn't spend on cigarettes today.",
    "Tell a friend or family member about your goal — social support boosts your motivation.",
    "Celebrate small wins! 3 days is already a huge personal achievement.",
    "Go for a 10-minute walk when a craving hits — it passes faster than you think.",
    "Track your streak every day. Watching the number grow is incredibly motivating.",
    "You're building a new identity: someone who doesn't need cigarettes to relax.",
    "Cravings last only 3–5 minutes. Ride it out — you are stronger than you think.",
    "Think about what you'll do with the extra money you save each week.",
    "Your fitness is improving. Take the stairs today and notice the difference!",
    "Keep your hands busy: knit, draw, write, or fidget — it really helps.",
    "Avoid triggers for now: coffee breaks with smokers, post-meal habits — swap them.",
    "At the two-week mark, many people say cravings are noticeably weaker. Keep going!",
    "Write down your WHY — the real reason you started this journey — and read it daily.",
    "Breathing exercises (4s in, hold 4s, out 4s) can calm craving spikes instantly.",
    "Share your progress with someone. Accountability is a powerful motivator.",
    "Imagine yourself 6 months from now: fitter, richer, and free from the habit.",
    "One month in — you have officially broken the chemical routine loop. Amazing!",
    "Try a new hobby or sport to fill the time you used to spend smoking.",
    "You're saving real money. In one month, a pack-a-day smoker saves €60+!",
    "A craving is just your brain trying an old habit. Redirect it — you are in control.",
    "Celebrate milestones: a dinner out, a movie, a gift to yourself from your savings.",
    "Deep breathing is a free tool — use it every time a craving hits.",
    "You are part of millions who successfully quit every year. You've got this!",
    "Your skin is getting better, your breath is fresher. Those around you notice it!",
    "At 3 months, consider planting a sunflower — a symbol of your new smoke-free life 🌻",
    "You are not 'giving something up' — you are gaining your health, freedom, and money back."
)
