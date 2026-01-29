package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for playback-related settings.
 * Provides platform-aware defaults (TV vs Phone).
 */
interface PlaybackSettingsRepository {
    /**
     * Whether to automatically start playback when app launches.
     * Default: true on TV, false on Phone.
     */
    val autoPlayEnabled: Flow<Boolean>

    /**
     * Whether to stop playback when app goes to background (TV only).
     * Default: true on TV.
     */
    val autoStopOnBackgroundEnabled: Flow<Boolean>

    /**
     * Sets auto-play on start preference.
     */
    suspend fun setAutoPlayEnabled(enabled: Boolean)

    /**
     * Sets auto-stop on background preference (TV only).
     */
    suspend fun setAutoStopOnBackgroundEnabled(enabled: Boolean)
}
