package com.rovenskyi.radiozavr.core.widget

import kotlinx.serialization.Serializable

/**
 * Time window defining when a widget is eligible to display.
 *
 * @property days Days of the week. Empty list means every day.
 * @property startHour Start hour inclusive (0-23).
 * @property endHour End hour exclusive (1-24).
 */
@Serializable
data class TimeWindow(
    val days: List<ScheduleDay> = emptyList(),
    val startHour: Int = 0,
    val endHour: Int = 24,
)
