package com.rovenskyi.radiozavr.data.widgetscheduler

import android.content.Context
import com.rovenskyi.radiozavr.core.widget.WidgetManagerConfig
import com.rovenskyi.radiozavr.core.widget.WidgetScheduleEntry
import com.rovenskyi.radiozavr.domain.widgetscheduler.WidgetConfigSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads default widget scheduler config from assets JSON.
 *
 * Uses `ignoreUnknownKeys` for forward compatibility — new fields
 * added by backend won't break older app versions.
 *
 * Schedule entries are parsed individually so that a single entry
 * with an unknown WidgetType won't break the entire config.
 */
@Singleton
class LocalWidgetConfigSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : WidgetConfigSource {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getConfig(): WidgetManagerConfig? {
        return try {
            val jsonString = context.assets
                .open(CONFIG_PATH)
                .bufferedReader()
                .use { it.readText() }
            parseConfig(jsonString)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Parses config with graceful schedule entry handling.
     *
     * Top-level fields are parsed normally. Schedule entries are parsed
     * one by one — entries with unknown enum values are silently skipped
     * instead of failing the entire config.
     */
    private fun parseConfig(jsonString: String): WidgetManagerConfig {
        val root = json.parseToJsonElement(jsonString).jsonObject
        val scheduleArray = root["schedule"]?.jsonArray.orEmpty()

        val entries = scheduleArray.mapNotNull { element ->
            try {
                json.decodeFromJsonElement(WidgetScheduleEntry.serializer(), element)
            } catch (_: Exception) {
                null
            }
        }

        val configWithoutSchedule = json.decodeFromJsonElement(
            WidgetManagerConfig.serializer(),
            JsonObject(root.toMutableMap().apply { remove("schedule") }),
        )
        return configWithoutSchedule.copy(schedule = entries)
    }

    private companion object {
        const val CONFIG_PATH = "config/widget_scheduler_default.json"
    }
}
