package com.rovenskyi.radiozavr.core.widget

import kotlinx.serialization.Serializable

/**
 * Days of the week for widget scheduling time windows.
 */
@Serializable
enum class ScheduleDay {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN,
}
