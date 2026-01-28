package com.rovenskyi.radiolux.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dimension values for consistent spacing and sizing.
 * TV uses larger values for 10-foot viewing distance.
 */
@Immutable
data class Dimensions(
    // Padding
    val paddingXSmall: Dp = 4.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val paddingLarge: Dp = 24.dp,
    val paddingXLarge: Dp = 32.dp,

    // Spacing between elements
    val spacingXSmall: Dp = 4.dp,
    val spacingSmall: Dp = 8.dp,
    val spacingMedium: Dp = 16.dp,
    val spacingLarge: Dp = 24.dp,
    val spacingXLarge: Dp = 32.dp,

    // Corner radius
    val cornerSmall: Dp = 4.dp,
    val cornerMedium: Dp = 8.dp,
    val cornerLarge: Dp = 16.dp,
    val cornerXLarge: Dp = 24.dp,

    // Icon sizes
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val iconXLarge: Dp = 48.dp,

    // Button sizes
    val buttonHeightSmall: Dp = 32.dp,
    val buttonHeightMedium: Dp = 48.dp,
    val buttonHeightLarge: Dp = 56.dp,

    // Play button (main action)
    val playButtonSize: Dp = 80.dp,
    val playButtonIconSize: Dp = 48.dp,

    // Focus indicator
    val focusBorderWidth: Dp = 3.dp,

    // Card sizes
    val cardMinHeight: Dp = 80.dp,

    // Safe area margins (TV overscan)
    val safeAreaHorizontal: Dp = 0.dp,
    val safeAreaVertical: Dp = 0.dp,
)

/**
 * Phone dimensions - standard mobile sizes
 */
val PhoneDimensions = Dimensions()

/**
 * TV dimensions - scaled up for 10-foot experience
 * ~1.5x scale factor for most elements
 */
val TvDimensions = Dimensions(
    // Padding
    paddingXSmall = 6.dp,
    paddingSmall = 12.dp,
    paddingMedium = 24.dp,
    paddingLarge = 36.dp,
    paddingXLarge = 48.dp,

    // Spacing
    spacingXSmall = 6.dp,
    spacingSmall = 12.dp,
    spacingMedium = 24.dp,
    spacingLarge = 36.dp,
    spacingXLarge = 48.dp,

    // Corner radius
    cornerSmall = 6.dp,
    cornerMedium = 12.dp,
    cornerLarge = 24.dp,
    cornerXLarge = 36.dp,

    // Icon sizes
    iconSmall = 24.dp,
    iconMedium = 36.dp,
    iconLarge = 48.dp,
    iconXLarge = 72.dp,

    // Button sizes
    buttonHeightSmall = 48.dp,
    buttonHeightMedium = 72.dp,
    buttonHeightLarge = 84.dp,

    // Play button (main action) - larger for TV
    playButtonSize = 120.dp,
    playButtonIconSize = 72.dp,

    // Focus indicator - more visible on TV
    focusBorderWidth = 4.dp,

    // Card sizes
    cardMinHeight = 120.dp,

    // Safe area margins (TV overscan protection)
    safeAreaHorizontal = 48.dp,
    safeAreaVertical = 27.dp,
)

/**
 * CompositionLocal for providing dimensions throughout the app
 */
val LocalDimensions = staticCompositionLocalOf { PhoneDimensions }
