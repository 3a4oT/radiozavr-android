package com.rovenskyi.radio_lux_fm_lviv_streamer.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rovenskyi.radiolux.core.models.riddle.RiddleCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.riddleHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "riddle_history",
)

/**
 * DataStore for persisting shown riddle indices.
 * Ensures users don't see the same riddles after app restart.
 *
 * Storage format:
 * - "shown_{CATEGORY_NAME}" -> "1,5,12,34" (comma-separated indices)
 * - "current_category_index" -> 0 (for rotation)
 */
@Singleton
class RiddleHistoryDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val currentCategoryIndexKey = intPreferencesKey("current_category_index")

    private fun shownIndicesKey(category: RiddleCategory): Preferences.Key<String> =
        stringPreferencesKey("shown_${category.name}")

    /**
     * Get the current category index for rotation.
     */
    suspend fun getCurrentCategoryIndex(): Int =
        context.riddleHistoryDataStore.data
            .map { prefs -> prefs[currentCategoryIndexKey] ?: 0 }
            .first()

    /**
     * Increment and persist the category index.
     */
    suspend fun incrementCategoryIndex(): Int {
        var newIndex = 0
        context.riddleHistoryDataStore.edit { prefs ->
            val current = prefs[currentCategoryIndexKey] ?: 0
            newIndex = (current + 1) % RiddleCategory.entries.size
            prefs[currentCategoryIndexKey] = newIndex
        }
        return newIndex
    }

    /**
     * Get shown riddle indices for a category.
     */
    suspend fun getShownIndices(category: RiddleCategory): Set<Int> =
        context.riddleHistoryDataStore.data
            .map { prefs ->
                prefs[shownIndicesKey(category)]
                    ?.split(",")
                    ?.filter { it.isNotBlank() }
                    ?.mapNotNull { it.toIntOrNull() }
                    ?.toSet()
                    ?: emptySet()
            }
            .first()

    /**
     * Add a shown index for a category.
     */
    suspend fun addShownIndex(category: RiddleCategory, index: Int) {
        context.riddleHistoryDataStore.edit { prefs ->
            val current = prefs[shownIndicesKey(category)]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toMutableSet()
                ?: mutableSetOf()
            current.add(index.toString())
            prefs[shownIndicesKey(category)] = current.joinToString(",")
        }
    }

    /**
     * Clear shown indices for a specific category.
     */
    suspend fun clearCategoryHistory(category: RiddleCategory) {
        context.riddleHistoryDataStore.edit { prefs ->
            prefs.remove(shownIndicesKey(category))
        }
    }

    /**
     * Clear all history (all categories).
     * Called when all riddles across all categories have been shown.
     */
    suspend fun clearAllHistory() {
        context.riddleHistoryDataStore.edit { prefs ->
            RiddleCategory.entries.forEach { category ->
                prefs.remove(shownIndicesKey(category))
            }
        }
    }
}
