package com.rovenskyi.radiolux.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.rovenskyi.radiolux.core.models.theme.ThemeMode
import java.time.LocalTime

/**
 * CompositionLocal for tracking if current theme is dark
 */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

/**
 * CompositionLocal for tracking if running on TV
 */
val LocalIsTv = staticCompositionLocalOf { false }

/**
 * Determines if dark mode should be active based on ThemeMode setting.
 * AUTO mode uses time-based switching: 9:00-18:00 = Light, otherwise Dark.
 */
fun shouldUseDarkMode(themeMode: ThemeMode): Boolean {
    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AUTO -> {
            val hour = LocalTime.now().hour
            hour < 9 || hour >= 18
        }
    }
}

/**
 * Main theme composable for Radio Lux FM.
 *
 * @param themeMode Current theme mode (LIGHT, DARK, AUTO)
 * @param isTv Whether running on TV device (affects dimensions and typography)
 * @param content The content to theme
 */
@Composable
fun RadioLuxTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    isTv: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isDark = shouldUseDarkMode(themeMode)
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val typography = if (isTv) TvTypography else AppTypography
    val dimensions = if (isTv) TvDimensions else PhoneDimensions

    CompositionLocalProvider(
        LocalIsDarkTheme provides isDark,
        LocalIsTv provides isTv,
        LocalDimensions provides dimensions,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }
}

/**
 * Theme accessor object for convenient access to theme values.
 */
object RadioLuxTheme {

    /**
     * Current gradient palette based on dark mode state.
     */
    val gradientPalette: List<androidx.compose.ui.graphics.Color>
        @Composable
        @ReadOnlyComposable
        get() = GradientPalette.forDarkMode(LocalIsDarkTheme.current)

    /**
     * Current dimensions based on device type (TV vs Phone).
     */
    val dimensions: Dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current

    /**
     * Whether current theme is dark.
     */
    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDarkTheme.current

    /**
     * Whether running on TV device.
     */
    val isTv: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsTv.current
}
