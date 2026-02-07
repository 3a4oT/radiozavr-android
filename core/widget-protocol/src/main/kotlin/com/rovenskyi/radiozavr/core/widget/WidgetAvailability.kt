package com.rovenskyi.radiozavr.core.widget

/**
 * Widget data readiness reported to the manager.
 *
 * The manager only schedules widgets that are [AVAILABLE].
 * A widget becomes [UNAVAILABLE] when it has no data, encounters
 * an error, or its cached data exceeds the staleness threshold.
 */
enum class WidgetAvailability {
    /** Has data and is ready to be displayed. */
    AVAILABLE,

    /** Cannot display — no data, error, or stale beyond threshold. */
    UNAVAILABLE,
}
