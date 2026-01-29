package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

/**
 * Repository for platform-related information.
 * Provides abstraction over Android-specific platform detection.
 */
interface PlatformRepository {
    /**
     * Returns true if the app is running on Android TV (Leanback device).
     */
    val isTv: Boolean
}
