package com.rovenskyi.radiozavr.domain.playback

import com.rovenskyi.radiozavr.core.models.riddle.RiddleAnswerMode
import com.rovenskyi.radiozavr.core.models.riddle.RiddleInterval
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
     * Interval for riddle rotation.
     * Default: 30 seconds.
     */
    val riddleInterval: Flow<RiddleInterval>

    /**
     * Mode for revealing riddle answers.
     * Default: AUTOMATIC.
     */
    val riddleAnswerMode: Flow<RiddleAnswerMode>

    /**
     * Sets auto-play on start preference.
     */
    suspend fun setAutoPlayEnabled(enabled: Boolean)

    /**
     * Sets auto-stop on background preference (TV only).
     */
    suspend fun setAutoStopOnBackgroundEnabled(enabled: Boolean)

    /**
     * Sets riddle rotation interval.
     */
    suspend fun setRiddleInterval(interval: RiddleInterval)

    /**
     * Sets riddle answer reveal mode.
     */
    suspend fun setRiddleAnswerMode(mode: RiddleAnswerMode)
}
