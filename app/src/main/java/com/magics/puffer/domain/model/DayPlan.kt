package com.magics.puffer.domain.model

/**
 * Represents one planned day in the gradual reduction schedule.
 */
data class DayPlan(
    val dayNumber: Int,        // 1-based day index from start
    val date: String,          // yyyy-MM-dd
    val targetCigarettes: Int, // How many cigarettes allowed this day
    val phase: QuitPhase       // Which phase of the plan this falls in
)

/**
 * Phases of the smoking reduction journey.
 * Language is kept motivational — no medical terms.
 */
enum class QuitPhase {
    REDUCTION,   // Gradual daily reduction phase
    ZERO,        // Reached zero — maintaining smoke-free status
    SEED_MODE    // 3+ months smoke-free — using sunflower seeds as habit replacement
}
