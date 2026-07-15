package com.rovenskyi.radiozavr.core.models.weather

/**
 * Simplified weather condition, derived from WMO weather interpretation codes.
 */
enum class WeatherCondition {
    CLEAR,
    PARTLY_CLOUDY,
    CLOUDY,
    FOG,
    DRIZZLE,
    RAIN,
    SNOW,
    THUNDERSTORM,
    UNKNOWN,
}
