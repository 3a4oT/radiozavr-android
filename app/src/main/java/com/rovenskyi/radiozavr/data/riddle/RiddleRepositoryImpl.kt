package com.rovenskyi.radiozavr.data.riddle

import com.rovenskyi.radiozavr.core.models.riddle.Riddle
import com.rovenskyi.radiozavr.core.models.riddle.RiddleCategory
import com.rovenskyi.radiozavr.domain.riddle.RiddleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

private const val CACHE_MAX_SIZE = 3

/**
 * Repository implementation that loads riddles from local assets.
 * Uses LRU cache to keep only [CACHE_MAX_SIZE] categories in memory.
 *
 * Features:
 * - Weighted random category selection (categories with more unseen riddles have higher chance)
 * - Persistence of shown riddles across app restarts
 * - Automatic reset when all riddles exhausted
 *
 * Designed for easy migration to Firebase Remote Config:
 * - Same JSON format works for both sources
 * - Add RiddleRemoteDataSource and prefer remote, fallback to assets
 */
@Singleton
class RiddleRepositoryImpl @Inject constructor(
    private val assetDataSource: RiddleAssetDataSource,
    private val historyDataStore: RiddleHistoryDataStore,
) : RiddleRepository {

    private val cacheMutex = Mutex()

    // LRU cache: keeps last N accessed categories
    private val cache = object : LinkedHashMap<RiddleCategory, List<Riddle>>(
        CACHE_MAX_SIZE,
        0.75f,
        true,
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<RiddleCategory, List<Riddle>>?): Boolean {
            return size > CACHE_MAX_SIZE
        }
    }

    // Cache for category sizes (doesn't change during runtime)
    private val categorySizeCache = mutableMapOf<RiddleCategory, Int>()

    override suspend fun getNextUniqueRiddle(): Riddle = withContext(Dispatchers.IO) {
        // Build weights for each category
        val categoryWeights = buildCategoryWeights()

        // If all categories exhausted, reset and rebuild weights
        val totalWeight = categoryWeights.values.sum()
        val finalWeights = if (totalWeight == 0) {
            historyDataStore.clearAllHistory()
            buildCategoryWeights()
        } else {
            categoryWeights
        }

        // Select category using weighted random
        val selectedCategory = selectWeightedCategory(finalWeights)

        // Get unseen riddle from selected category
        getUnseenRiddleFromCategory(selectedCategory)
    }

    override suspend fun getRandomRiddle(): Riddle = withContext(Dispatchers.IO) {
        val category = RiddleCategory.entries.random()
        val riddles = getRiddlesByCategory(category)
        riddles.randomOrNull() ?: createFallbackRiddle()
    }

    override suspend fun getRiddlesByCategory(category: RiddleCategory): List<Riddle> =
        withContext(Dispatchers.IO) {
            cacheMutex.withLock {
                cache.getOrPut(category) {
                    runCatching { assetDataSource.loadCategory(category) }
                        .getOrDefault(emptyList())
                }
            }
        }

    override fun getAvailableCategories(): List<RiddleCategory> = RiddleCategory.entries

    override suspend fun clearHistory() {
        historyDataStore.clearAllHistory()
    }

    /**
     * Build map of category -> available riddle count (weight).
     * Higher weight = more unseen riddles = higher chance of selection.
     */
    private suspend fun buildCategoryWeights(): Map<RiddleCategory, Int> {
        val weights = mutableMapOf<RiddleCategory, Int>()

        for (category in RiddleCategory.entries) {
            val totalCount = getCategorySize(category)
            val shownIndices = historyDataStore.getShownIndices(category)
            val availableCount = (totalCount - shownIndices.size).coerceAtLeast(0)
            weights[category] = availableCount
        }

        return weights
    }

    /**
     * Get total riddle count for a category (cached).
     */
    private suspend fun getCategorySize(category: RiddleCategory): Int {
        return categorySizeCache.getOrPut(category) {
            getRiddlesByCategory(category).size
        }
    }

    /**
     * Select a category using weighted random selection.
     */
    private fun selectWeightedCategory(weights: Map<RiddleCategory, Int>): RiddleCategory {
        val totalWeight = weights.values.sum()
        if (totalWeight == 0) {
            return RiddleCategory.entries.random()
        }

        var randomValue = Random.nextInt(totalWeight)

        for ((category, weight) in weights) {
            randomValue -= weight
            if (randomValue < 0) {
                return category
            }
        }

        // Fallback (should not reach here)
        return weights.keys.first()
    }

    /**
     * Get a random unseen riddle from the specified category.
     * Marks the riddle as shown in history.
     */
    private suspend fun getUnseenRiddleFromCategory(category: RiddleCategory): Riddle {
        val riddles = getRiddlesByCategory(category)
        if (riddles.isEmpty()) {
            return createFallbackRiddle()
        }

        val shownIndices = historyDataStore.getShownIndices(category)
        val availableIndices = riddles.indices.filter { it !in shownIndices }

        // If all shown in this category, clear and pick any
        val selectedIndex = if (availableIndices.isEmpty()) {
            historyDataStore.clearCategoryHistory(category)
            riddles.indices.random()
        } else {
            availableIndices.random()
        }

        // Mark as shown
        historyDataStore.addShownIndex(category, selectedIndex)

        return riddles[selectedIndex]
    }

    private fun createFallbackRiddle(): Riddle = Riddle(
        question = "Що росте вниз головою?",
        answer = "Бурулька",
        emoji = "🧊",
    )
}
