package com.rovenskyi.radiozavr.core.models.weather

/**
 * A forecasted reading for a single upcoming hour.
 */
data class HourlyForecast(
    val hourOffset: Int,
    val temperatureCelsius: Double,
    val condition: WeatherCondition,
)
