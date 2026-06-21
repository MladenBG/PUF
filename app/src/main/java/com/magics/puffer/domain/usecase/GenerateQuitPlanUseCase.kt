package com.magics.puffer.domain.usecase

import com.magics.puffer.domain.model.DayPlan
import com.magics.puffer.domain.model.QuitPhase
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.DatePeriod
import javax.inject.Inject

/**
 * Generates the full daily quit plan from the user's starting settings.
 *
 * Strategy: Gradual daily reduction.
 * - Phase 1 (REDUCTION): cigarettes decrease by ~1 every 2-3 days
 *   until they reach 0 by the target date.
 * - Phase 2 (ZERO): days after reaching 0, marked as smoke-free maintenance.
 *
 * This is a personal motivation tracker — not a medical prescription.
 */
class GenerateQuitPlanUseCase @Inject constructor() {

    /**
     * @param startDateStr  yyyy-MM-dd when the user began the plan
     * @param startingCount Number of cigarettes per day at the start
     * @param totalDays     How many days the plan should span (e.g. 90)
     * @return List of [DayPlan] for each day of the plan
     */
    fun execute(
        startDateStr: String,
        startingCount: Int,
        totalDays: Int
    ): List<DayPlan> {
        if (startDateStr.isBlank() || startingCount <= 0 || totalDays <= 0) return emptyList()

        val startDate = LocalDate.parse(startDateStr)
        val plans = mutableListOf<DayPlan>()

        // Calculate how many days to go from startingCount to 0
        // We reduce by 1 cigarette every N days where N = totalDays / startingCount
        val daysPerStep = if (startingCount < totalDays) totalDays.toFloat() / startingCount else 1f

        for (dayIndex in 0 until totalDays) {
            val date = startDate.plus(DatePeriod(days = dayIndex))
            val dayNumber = dayIndex + 1

            // How many cigarettes should be allowed this day
            val reduced = (dayIndex / daysPerStep).toInt()
            val target = (startingCount - reduced).coerceAtLeast(0)

            val phase = if (target == 0) QuitPhase.ZERO else QuitPhase.REDUCTION

            plans.add(
                DayPlan(
                    dayNumber = dayNumber,
                    date = date.toString(),
                    targetCigarettes = target,
                    phase = phase
                )
            )
        }
        return plans
    }

    /**
     * Get today's target from the plan list, or null if today is outside the plan range.
     */
    fun getTodayTarget(plan: List<DayPlan>, todayStr: String): DayPlan? {
        return plan.firstOrNull { it.date == todayStr }
    }
}
