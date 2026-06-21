package com.magics.puffer.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Milestone badge that the user can earn during their journey.
 * Earned by hitting streak or reduction targets — purely motivational.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val requiredDays: Int,        // Smoke-free days needed to unlock
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null
)

/**
 * Predefined list of all achievements available in the app.
 * Copy is motivational — no clinical or medical language.
 */
object Achievements {
    val all = listOf(
        Achievement(
            id = "first_day",
            title = "First Step",
            description = "Completed your very first smoke-free day. The journey of a thousand miles begins with a single step!",
            emoji = "🌱",
            requiredDays = 1
        ),
        Achievement(
            id = "three_days",
            title = "3-Day Warrior",
            description = "3 days strong! Your body is already thanking you. Taste and smell are starting to come back.",
            emoji = "⚡",
            requiredDays = 3
        ),
        Achievement(
            id = "one_week",
            title = "Week Champion",
            description = "One full week! Your circulation has noticeably improved. Celebrate — you've earned it!",
            emoji = "🏆",
            requiredDays = 7
        ),
        Achievement(
            id = "two_weeks",
            title = "Two-Week Hero",
            description = "Two weeks smoke-free! Your lungs are working better with every breath you take.",
            emoji = "💪",
            requiredDays = 14
        ),
        Achievement(
            id = "one_month",
            title = "Monthly Master",
            description = "One whole month! You've broken the habit loop. Your savings are stacking up!",
            emoji = "🌟",
            requiredDays = 30
        ),
        Achievement(
            id = "two_months",
            title = "Double Month",
            description = "60 days! You're building a new, healthier lifestyle. Keep the momentum!",
            emoji = "🔥",
            requiredDays = 60
        ),
        Achievement(
            id = "three_months",
            title = "90-Day Legend 🌻",
            description = "3 months smoke-free! SEED MODE unlocked — replace cravings with sunflower seeds for the next 6 months!",
            emoji = "🌻",
            requiredDays = 90
        ),
        Achievement(
            id = "six_months",
            title = "Half-Year Hero",
            description = "Half a year of a smoke-free life! You've transformed your daily routine completely.",
            emoji = "💎",
            requiredDays = 180
        ),
        Achievement(
            id = "one_year",
            title = "Year Legend",
            description = "ONE FULL YEAR! You are an absolute inspiration. Share your story and inspire others!",
            emoji = "👑",
            requiredDays = 365
        )
    )
}
