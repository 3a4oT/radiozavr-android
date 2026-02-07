package com.rovenskyi.radiozavr.core.widget

/**
 * Urgency level reported by a widget to influence scheduling priority.
 *
 * Higher urgency overrides normal scheduling rules:
 * - [SCHEDULED] follows the regular interval/cooldown cycle.
 * - [ELEVATED] requests display within the next rotation cycle.
 * - [URGENT] can interrupt a [DisplayPhase.PRESENTING] widget immediately.
 */
enum class WidgetUrgency {
    /** Normal schedule — respects interval and cooldown. */
    SCHEDULED,

    /** Show sooner — within the next rotation cycle. */
    ELEVATED,

    /** Show immediately — can interrupt PRESENTING. */
    URGENT,
}
