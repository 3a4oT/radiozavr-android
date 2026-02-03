package com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository

import com.rovenskyi.radio_lux_fm_lviv_streamer.data.source.RiddleAssetDataSource
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RiddleRepository
import com.rovenskyi.radiolux.core.models.riddle.Riddle
import com.rovenskyi.radiolux.core.models.riddle.RiddleCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val CACHE_MAX_SIZE = 3

/**
 * Repository implementation that loads riddles from local assets.
 * Uses LRU cache to keep only [CACHE_MAX_SIZE] categories in memory.
 *
 * Designed for easy migration to Firebase Remote Config:
 * - Same JSON format works for both sources
 * - Add RiddleRemoteDataSource and prefer remote, fallback to assets
 */
@Singleton
class RiddleRepositoryImpl @Inject constructor(
    private val dataSource: RiddleAssetDataSource,
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

    override suspend fun getRandomRiddle(): Riddle = withContext(Dispatchers.IO) {
        val category = RiddleCategory.entries.random()
        val riddles = getRiddlesByCategory(category)
        riddles.randomOrNull() ?: createFallbackRiddle()
    }

    override suspend fun getRiddlesByCategory(category: RiddleCategory): List<Riddle> =
        withContext(Dispatchers.IO) {
            cacheMutex.withLock {
                cache.getOrPut(category) {
                    runCatching { dataSource.loadCategory(category) }
                        .getOrDefault(emptyList())
                }
            }
        }

    override fun getAvailableCategories(): List<RiddleCategory> = RiddleCategory.entries

    private fun createFallbackRiddle(): Riddle = Riddle(
        question = "Що росте вниз головою?",
        answer = "Бурулька",
        emoji = "🧊",
    )
}
