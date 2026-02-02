package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

import com.rovenskyi.radiolux.core.models.platform.DeviceType

/**
 * Repository for platform-related information.
 * Provides abstraction over Android-specific platform detection.
 */
interface PlatformRepository {
    /**
     * The detected device type based on system features and display characteristics.
     * Uses Google Play heuristics for phone/tablet/foldable classification.
     */
    val deviceType: DeviceType

    /**
     * Returns true if the app is running on Android TV (Leanback device).
     */
    val isTv: Boolean

    /**
     * Returns true for large screen devices: TV, Tablet, or Foldable.
     * Useful for UI decisions like showing auto-stop setting.
     */
    val isLargeScreen: Boolean
}
