package com.rovenskyi.radiozavr.core.models.platform

/**
 * Device type classification based on system features and display characteristics.
 *
 * Detection priority (highest to lowest):
 * 1. TV - FEATURE_LEANBACK
 * 2. Watch - FEATURE_WATCH
 * 3. Automotive - FEATURE_AUTOMOTIVE
 * 4. Foldable - FEATURE_SENSOR_HINGE_ANGLE + built-in display >= 600dp
 * 5. Tablet - built-in display >= 600dp, no hinge
 * 6. Phone - built-in display < 600dp
 *
 * Based on Google Play device classification heuristics:
 * https://android-developers.googleblog.com/2023/06/detecting-if-device-is-foldable-tablet.html
 */
enum class DeviceType(val analyticsValue: String) {
    TV("tv"),
    WATCH("watch"),
    AUTOMOTIVE("automotive"),
    FOLDABLE("foldable"),
    TABLET("tablet"),
    PHONE("phone"),
}
