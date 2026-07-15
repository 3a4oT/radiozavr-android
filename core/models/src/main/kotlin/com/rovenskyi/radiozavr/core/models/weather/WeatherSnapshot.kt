package com.rovenskyi.radiozavr.core.models.weather

/**
 * A single point-in-time weather reading for the widget.
 */
data class WeatherSnapshot(
    val temperatureCelsius: Double,
    val humidityPercent: Int,
    val condition: WeatherCondition,
    val hourlyForecast: List<HourlyForecast>,
    val fetchedAtMillis: Long,
)
