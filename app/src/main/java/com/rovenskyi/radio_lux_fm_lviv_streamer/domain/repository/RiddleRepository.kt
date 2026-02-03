package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

import com.rovenskyi.radiolux.core.models.riddle.Riddle
import com.rovenskyi.radiolux.core.models.riddle.RiddleCategory

/**
 * Repository for accessing riddles from various sources.
 * Currently loads from local assets, designed for easy migration to Firebase Remote Config.
 */
interface RiddleRepository {
    /**
     * Get a random riddle from any category.
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
}
