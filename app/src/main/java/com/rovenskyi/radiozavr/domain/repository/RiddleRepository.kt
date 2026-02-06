package com.rovenskyi.radiozavr.domain.repository

import com.rovenskyi.radiozavr.core.models.riddle.Riddle
import com.rovenskyi.radiozavr.core.models.riddle.RiddleCategory

/**
 * Repository for accessing riddles from various sources.
 * Currently loads from local assets, designed for easy migration to Firebase Remote Config.
 *
 * Features:
 * - Category rotation (cycles through categories sequentially)
 * - Unique riddles (no repeats until all shown)
 * - Persistence (survives app restart)
 */
interface RiddleRepository {
    /**
     * Get the next unique riddle with category rotation.
     *
     * Behavior:
     * 1. Rotates through categories sequentially
     * 2. Within each category, picks a random unseen riddle
     * 3. Persists shown riddles across app restarts
     * 4. Resets when ALL riddles in ALL categories have been shown
     */
    suspend fun getNextUniqueRiddle(): Riddle

    /**
     * Get a random riddle from any category.
     * Note: Does not guarantee uniqueness. Prefer [getNextUniqueRiddle] for widget use.
     */
    suspend fun getRandomRiddle(): Riddle

    /**
     * Get all riddles for a specific category.
     * Results are cached in memory (LRU, max 3 categories).
     */
    suspend fun getRiddlesByCategory(category: RiddleCategory): List<Riddle>

    /**
     * Get list of all available categories.
     */
    fun getAvailableCategories(): List<RiddleCategory>

    /**
     * Clear all shown riddle history.
     * Useful for testing or user-initiated reset.
     */
    suspend fun clearHistory()
}
