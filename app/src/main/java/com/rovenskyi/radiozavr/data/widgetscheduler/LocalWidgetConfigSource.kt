package com.rovenskyi.radiozavr.data.widgetscheduler

import android.content.Context
import com.rovenskyi.radiozavr.core.widget.WidgetManagerConfig
import com.rovenskyi.radiozavr.domain.widgetscheduler.WidgetConfigSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads default widget scheduler config from assets JSON.
 *
 * Uses `ignoreUnknownKeys` for forward compatibility — new fields
 * added by backend won't break older app versions.
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
            json.decodeFromString<WidgetManagerConfig>(jsonString)
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        const val CONFIG_PATH = "config/widget_scheduler_default.json"
    }
}
