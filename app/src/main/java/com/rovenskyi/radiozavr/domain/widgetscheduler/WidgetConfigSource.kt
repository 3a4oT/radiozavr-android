package com.rovenskyi.radiozavr.domain.widgetscheduler

import com.rovenskyi.radiozavr.core.widget.WidgetManagerConfig

/**
 * Source of widget scheduler configuration.
 *
 * Multiple sources can exist (local assets, remote API).
 * Repository decides priority and fallback order.
 */
interface WidgetConfigSource {
    suspend fun getConfig(): WidgetManagerConfig?
}
