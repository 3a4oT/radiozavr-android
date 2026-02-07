package com.rovenskyi.radiozavr.core.widget

import kotlinx.coroutines.flow.StateFlow

/**
 * Contract between a widget and the Widget Manager.
 *
 * Each widget implements this interface to report its state reactively.
 * The manager observes [availability], [displayPhase], and [urgency]
 * to decide scheduling. UI rendering is handled separately by mapping
 * [WidgetType] to a Composable in the presentation layer.
 */
interface WidgetProtocol {

    val type: WidgetType

    val availability: StateFlow<WidgetAvailability>

    val displayPhase: StateFlow<DisplayPhase>

    val urgency: StateFlow<WidgetUrgency>

    /**
     * Epoch millis of the last data update, or `null` for local-only widgets.
     * Manager checks this against [WidgetScheduleEntry.maxStalenessMin].
     */
    val dataFreshnessMs: StateFlow<Long?>

    /** Called when this widget becomes visible. */
    fun onActivated()

    /** Called when this widget is being hidden. */
    fun onDeactivated()
}
