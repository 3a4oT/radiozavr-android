package com.rovenskyi.radiozavr.data.widgetscheduler

import com.rovenskyi.radiozavr.BuildConfig
import com.rovenskyi.radiozavr.core.widget.WidgetManagerConfig
import com.rovenskyi.radiozavr.domain.widgetscheduler.WidgetConfigRepository
import com.rovenskyi.radiozavr.domain.widgetscheduler.WidgetConfigSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Combines config sources and filters entries for current app version.
 *
 * Phase 1: local source only.
 * Phase 2: remote source with local fallback.
 */
@Singleton
class WidgetConfigRepositoryImpl @Inject constructor(
    private val localSource: WidgetConfigSource,
) : WidgetConfigRepository {

    override fun getConfig(): Flow<WidgetManagerConfig> = flow {
        val config = localSource.getConfig() ?: WidgetManagerConfig()
        emit(config.filterForCurrentApp())
    }
}

/**
 * Filters schedule entries that this app version can handle.
 *
 * Drops entries whose `minAppVersion` exceeds the running app.
 * Unknown WidgetType values are already filtered during JSON parsing
 * (see [LocalWidgetConfigSource]).
 *
 * If `schemaVersion` exceeds [SUPPORTED_SCHEMA_VERSION],
 * returns empty defaults — backend bumps schema only on breaking changes.
 */
internal fun WidgetManagerConfig.filterForCurrentApp(): WidgetManagerConfig {
    if (schemaVersion > SUPPORTED_SCHEMA_VERSION) return WidgetManagerConfig()

    val appVersion = BuildConfig.VERSION_NAME
    return copy(
        schedule = schedule.filter { entry ->
            isVersionCompatible(appVersion, entry.minAppVersion)
        },
    )
}

/**
 * Compares semantic versions (major.minor.patch).
 * Returns `true` if [appVersion] >= [requiredVersion].
 */
internal fun isVersionCompatible(appVersion: String, requiredVersion: String): Boolean {
    val app = appVersion.toVersionParts()
    val required = requiredVersion.toVersionParts()

    for (i in 0 until maxOf(app.size, required.size)) {
        val a = app.getOrElse(i) { 0 }
        val r = required.getOrElse(i) { 0 }
        if (a != r) return a > r
    }
    return true
}

private fun String.toVersionParts(): List<Int> =
    split(".").mapNotNull { it.toIntOrNull() }

private const val SUPPORTED_SCHEMA_VERSION = 1
