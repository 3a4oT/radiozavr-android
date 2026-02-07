package com.rovenskyi.radiozavr.core.widget

import kotlinx.serialization.Serializable

/**
 * Network dependency mode for a widget's data source.
 *
 * Determines offline behavior and staleness handling.
 */
@Serializable
enum class NetworkMode {
    /** Works fully offline — data bundled in assets (e.g. Riddles). */
    LOCAL,

    /** Can show stale cached data when offline (e.g. Weather). */
    CACHED,

    /** Requires active network connection. */
    ONLINE,
}
