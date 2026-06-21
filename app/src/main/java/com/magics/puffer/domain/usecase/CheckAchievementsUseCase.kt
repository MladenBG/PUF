package com.magics.puffer.domain.usecase

import com.magics.puffer.domain.model.Achievement
import com.magics.puffer.domain.model.Achievements
import javax.inject.Inject

/**
 * Checks which achievements are unlocked based on the number of consecutive smoke-free days.
 * Seed mode unlock is triggered at 90 consecutive smoke-free days.
 */
class CheckAchievementsUseCase @Inject constructor() {

    /**
     * @param smokeFreeDays Total smoke-free days tracked in the app
     * @return List of achievements with [isUnlocked] flag set correctly
     */
    fun execute(smokeFreeDays: Int): List<Achievement> {
        return Achievements.all.map { achievement ->
            achievement.copy(isUnlocked = smokeFreeDays >= achievement.requiredDays)
        }
    }

    /**
     * Check if the user should have Seed Mode unlocked (90+ smoke-free days)
     */
    fun isSeedModeEligible(smokeFreeDays: Int): Boolean {
        return smokeFreeDays >= 90
    }

    /**
     * Returns the next achievement the user is working toward
     */
    fun getNextAchievement(smokeFreeDays: Int): Achievement? {
        return Achievements.all
            .filter { it.requiredDays > smokeFreeDays }
            .minByOrNull { it.requiredDays }
    }

    /**
     * How many days until the next achievement
     */
    fun daysUntilNext(smokeFreeDays: Int): Int? {
        val next = getNextAchievement(smokeFreeDays) ?: return null
        return next.requiredDays - smokeFreeDays
    }
}
