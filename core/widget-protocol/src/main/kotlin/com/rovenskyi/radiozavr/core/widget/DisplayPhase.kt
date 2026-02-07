package com.rovenskyi.radiozavr.core.widget

/**
 * Widget display lifecycle phase reported to the manager.
 *
 * Controls transition timing — the manager waits for [YIELDING]
 * before switching to another widget (unless urgency is [WidgetUrgency.URGENT]).
 */
enum class DisplayPhase {
    /** Widget is not displayed. */
    IDLE,

    /** Actively showing content — do not interrupt. */
    PRESENTING,

    /** Display cycle complete — OK to switch away. */
    YIELDING,
}
