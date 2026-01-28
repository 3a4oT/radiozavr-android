package com.rovenskyi.radiolux.core.theme

import androidx.compose.ui.graphics.Color

/**
 * Curated gradient palettes for relaxing animated backgrounds.
 * Each palette contains 6 colors that harmonize well together.
 */
object GradientPalette {

    /**
     * Dark mode palette - deep blues and purples
     * Calm, night-time feel, easy on eyes in dark environment
     */
    val Dark = listOf(
        Color(0xFF0D1B2A), // Deep navy
        Color(0xFF1B263B), // Slate blue
        Color(0xFF2D3A4F), // Storm blue
        Color(0xFF1A1A2E), // Dark purple
        Color(0xFF16213E), // Midnight blue
        Color(0xFF0F3460), // Ocean blue
    )

    /**
     * Light mode palette - warm creams and soft blues
     * Gentle, daytime feel, not harsh on eyes
     */
    val Light = listOf(
        Color(0xFFFAF9F6), // Warm white
        Color(0xFFF5F0E8), // Cream
        Color(0xFFE8E4DC), // Soft beige
        Color(0xFFE0E7EE), // Pale blue
        Color(0xFFD6E5F0), // Sky tint
        Color(0xFFF0E6D8), // Peach cream
    )

    /**
     * Get the appropriate palette based on dark mode state
     */
    fun forDarkMode(isDark: Boolean): List<Color> = if (isDark) Dark else Light
}
