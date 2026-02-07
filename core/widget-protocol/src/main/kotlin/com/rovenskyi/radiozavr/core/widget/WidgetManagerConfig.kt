package com.rovenskyi.radiozavr.core.widget

import kotlinx.serialization.Serializable

/**
 * Top-level widget manager configuration — the JSON contract with backend.
 *
 * Delivered via `GET /api/config` with ETag caching.
 * Local JSON in assets uses the same format as the API response.
 *
 * @property schemaVersion Bumped on breaking changes; client ignores unsupported versions.
 * @property minAppVersion Minimum app version that can parse this config.
 * @property primary Default widget shown between scheduled appearances.
 * @property startDelaySec Delay after app launch before scheduling begins (seconds).
 * @property transitionMs Crossfade duration between widgets (milliseconds).
 */
@Serializable
data class WidgetManagerConfig(
    val schemaVersion: Int = 1,
    val minAppVersion: String = "0.1.0",
    val primary: WidgetType = WidgetType.RIDDLE,
    val startDelaySec: Int = 30,
    val transitionMs: Int = 500,
    val schedule: List<WidgetScheduleEntry> = emptyList(),
)
