package com.rovenskyi.radio_lux_fm_lviv_streamer.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes organized by domain.
 * Using sealed interfaces for grouping and Kotlin Serialization for Navigation 2.8+.
 */
sealed interface AppRoute

// =============================================================================
// Main Flow
// =============================================================================

@Serializable
data object RadioPlayer : AppRoute

// =============================================================================
// Settings Flow
// =============================================================================

@Serializable
data object Settings : AppRoute

@Serializable
data object Theme : AppRoute

@Serializable
data object Language : AppRoute
