package com.rovenskyi.radiozavr.core.widget

import kotlinx.serialization.Serializable

/**
 * Per-widget scheduling configuration — part of the JSON contract with backend.
 *
 * @property isPrimary Primary widget fills gaps between scheduled ones.
 * @property minDisplaySec Minimum time before the manager can switch away (seconds).
 * @property intervalMin Target interval between appearances (minutes).
 * @property cooldownMin Minimum gap after hiding before next display (minutes).
 * @property priority Higher value = higher scheduling priority.
 * @property maxStalenessMin Maximum cached data age before marking unavailable (minutes).
 */
@Serializable
data class WidgetScheduleEntry(
    val type: WidgetType,
    val minAppVersion: String = "0.1.0",
    val enabled: Boolean = true,
    val isPrimary: Boolean = false,
    val displayDurationSec: Int = 25,
    val minDisplaySec: Int = 5,
    val intervalMin: Int = 60,
    val cooldownMin: Int = 15,
    val priority: Int = 0,
    val networkMode: NetworkMode = NetworkMode.LOCAL,
    val maxStalenessMin: Int = 120,
    val timeWindows: List<TimeWindow> = listOf(TimeWindow()),
)
