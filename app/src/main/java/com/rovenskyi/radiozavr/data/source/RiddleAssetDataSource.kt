package com.rovenskyi.radiozavr.data.source

import android.content.Context
import com.rovenskyi.radiolux.core.models.riddle.Riddle
import com.rovenskyi.radiolux.core.models.riddle.RiddleCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source that loads riddles from JSON files in assets/riddles/.
 * Each category has its own JSON file with an array of Riddle objects.
 */
@Singleton
class RiddleAssetDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Load all riddles for a given category from assets.
     * @throws Exception if file not found or JSON parsing fails
     */
    fun loadCategory(category: RiddleCategory): List<Riddle> {
        val inputStream = context.assets.open("riddles/${category.fileName}")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return json.decodeFromString<List<Riddle>>(jsonString)
    }
}
