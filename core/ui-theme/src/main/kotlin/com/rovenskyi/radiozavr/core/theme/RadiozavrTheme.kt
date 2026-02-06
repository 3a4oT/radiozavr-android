package com.rovenskyi.radiozavr.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.rovenskyi.radiozavr.core.models.theme.ThemeMode
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * CompositionLocal for tracking if current theme is dark
 */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

/**
 * CompositionLocal for tracking if running on TV
 */
val LocalIsTv = staticCompositionLocalOf { false }

/**
 * CompositionLocal for TV focus indicator color.
 * Changes based on theme for optimal visibility.
 */
val LocalTvFocusColor = staticCompositionLocalOf { FocusColors.Dark }

private const val LIGHT_START_HOUR = 9
private const val DARK_START_HOUR = 18

/**
 * Determines if dark mode should be active based on current time.
 * Light mode: 9:00-18:00, Dark mode: 18:00-9:00
 */
private fun isDarkByTime(hour: Int): Boolean = hour < LIGHT_START_HOUR || hour >= DARK_START_HOUR

/**
 * Calculates milliseconds until the next theme switch time (9:00 or 18:00).
 */
private fun millisUntilNextThemeChange(): Long {
    val now = LocalDateTime.now()
    val hour = now.hour

    val nextChangeTime = if (hour < LIGHT_START_HOUR) {
        // Before 9:00 → next change at 9:00 today
        LocalDateTime.of(LocalDate.now(), LocalTime.of(LIGHT_START_HOUR, 0))
    } else if (hour < DARK_START_HOUR) {
        // Between 9:00-18:00 → next change at 18:00 today
        LocalDateTime.of(LocalDate.now(), LocalTime.of(DARK_START_HOUR, 0))
    } else {
        // After 18:00 → next change at 9:00 tomorrow
        LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(LIGHT_START_HOUR, 0))
    }

    return ChronoUnit.MILLIS.between(now, nextChangeTime).coerceAtLeast(1000L)
}

/**
 * Composable that returns current dark mode state for AUTO theme
 * and automatically recomposes when time threshold is crossed.
 */
@Composable
private fun rememberAutoDarkMode(): Boolean {
    var isDark by remember { mutableStateOf(isDarkByTime(LocalTime.now().hour)) }

    LaunchedEffect(Unit) {
        while (true) {
            val delayMs = millisUntilNextThemeChange()
            delay(delayMs)
            isDark = isDarkByTime(LocalTime.now().hour)
        }
    }

    return isDark
}

/**
 * Determines if dark mode should be active based on ThemeMode setting.
 * AUTO mode uses time-based switching with real-time updates.
 */
@Composable
fun shouldUseDarkMode(themeMode: ThemeMode): Boolean {
    val autoDark = rememberAutoDarkMode()
    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AUTO -> autoDark
    }
}

/**
 * Main theme composable for Radiozavr.
 *
 * @param themeMode Current theme mode (LIGHT, DARK, AUTO)
 * @param isTv Whether running on TV device (affects dimensions and typography)
 * @param content The content to theme
 */
@Composable
fun RadiozavrTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    isTv: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isDark = shouldUseDarkMode(themeMode)
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val typography = if (isTv) TvTypography else AppTypography
    val dimensions = if (isTv) TvDimensions else PhoneDimensions
    val focusColor = if (isDark) FocusColors.Dark else FocusColors.Light

    CompositionLocalProvider(
        LocalIsDarkTheme provides isDark,
        LocalIsTv provides isTv,
        LocalDimensions provides dimensions,
        LocalTvFocusColor provides focusColor,
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
object RadiozavrTheme {

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

    /**
     * Current TV focus indicator color based on theme.
     */
    val tvFocusColor: androidx.compose.ui.graphics.Color
        @Composable
        @ReadOnlyComposable
        get() = LocalTvFocusColor.current
}
