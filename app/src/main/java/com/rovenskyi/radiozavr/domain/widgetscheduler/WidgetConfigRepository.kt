package com.rovenskyi.radiozavr.domain.widgetscheduler

import com.rovenskyi.radiozavr.core.widget.WidgetManagerConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repository for widget scheduler configuration.
 *
 * Combines multiple config sources with version filtering.
 * Phase 1: local assets only. Phase 2: remote API with local fallback.
 */
interface WidgetConfigRepository {
    fun getConfig(): Flow<WidgetManagerConfig>
}
