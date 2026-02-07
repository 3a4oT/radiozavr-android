package com.rovenskyi.radiozavr.core.widget

import kotlinx.serialization.Serializable

/**
 * Types of widgets available in the slideshow rotation.
 */
@Serializable
enum class WidgetType {
    RIDDLE,
    WEATHER,
    GEOMAGNETIC,
    PREDICTIONS,
}
