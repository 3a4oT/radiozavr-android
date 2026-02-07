package com.rovenskyi.radiozavr.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Brand colors - Lux FM identity
 */
object BrandColors {
    val Primary = Color(0xFFE63946) // Warm red - main accent
    val PrimaryDark = Color(0xFFB71C1C) // Dark red
}

/**
 * Dark theme colors - optimized for TV viewing
 * Deep navy tones, easy on the eyes
 */
object DarkColors {
    val Background = Color(0xFF0D1B2A) // Deep navy
    val Surface = Color(0xFF1B263B) // Slate blue
    val SurfaceVariant = Color(0xFF2D3A4F) // Storm blue
    val OnBackground = Color(0xFFE0E1DD) // Soft white
    val OnSurface = Color(0xFFE0E1DD) // Soft white
    val OnSurfaceVariant = Color(0xFFB0B8C1) // Muted gray
}

/**
 * Light theme colors - warm and calm
 * Creamy beige tones, easy on the eyes
 */
object LightColors {
    val Background = Color(0xFFF0EBE3) // Creamy beige - calm, not too bright
    val Surface = Color(0xFFE8E4E0) // Warm sand
    val SurfaceVariant = Color(0xFFDDD9D5) // Soft stone
    val OnBackground = Color(0xFF2B2D42) // Dark charcoal
    val OnSurface = Color(0xFF2B2D42) // Dark charcoal
    val OnSurfaceVariant = Color(0xFF4A4E5A) // Medium gray
}

/**
 * TV Focus colors - high visibility for D-pad navigation
 * Different colors for each theme to ensure good contrast
 */
object FocusColors {
    val Dark = Color(0xFF4DD0E1) // Cyan 300 - bright on dark navy, calm
    val Light = Color(0xFF1565C0) // Deep Blue 800 - visible on cream, not harsh
}

/**
 * Material 3 color scheme for dark theme
 */
internal val DarkColorScheme = darkColorScheme(
    primary = BrandColors.Primary,
    onPrimary = Color.White,
    primaryContainer = BrandColors.PrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = DarkColors.SurfaceVariant,
    onSecondary = DarkColors.OnSurface,
    background = DarkColors.Background,
    onBackground = DarkColors.OnBackground,
    surface = DarkColors.Surface,
    onSurface = DarkColors.OnSurface,
    surfaceVariant = DarkColors.SurfaceVariant,
    onSurfaceVariant = DarkColors.OnSurfaceVariant,
)

/**
 * Material 3 color scheme for light theme
 */
internal val LightColorScheme = lightColorScheme(
    primary = BrandColors.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = BrandColors.PrimaryDark,
    secondary = LightColors.SurfaceVariant,
    onSecondary = LightColors.OnSurface,
    background = LightColors.Background,
    onBackground = LightColors.OnBackground,
    surface = LightColors.Surface,
    onSurface = LightColors.OnSurface,
    surfaceVariant = LightColors.SurfaceVariant,
    onSurfaceVariant = LightColors.OnSurfaceVariant,
)
