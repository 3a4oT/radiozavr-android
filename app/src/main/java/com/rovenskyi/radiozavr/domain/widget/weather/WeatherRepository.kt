package com.rovenskyi.radiozavr.domain.widget.weather

import com.rovenskyi.radiozavr.core.models.weather.WeatherSnapshot

/**
 * Provides the current weather for the station's city (Lviv).
 * Implementations are expected to cache results to avoid hammering the API.
 */
interface WeatherRepository {
    suspend fun getCurrentWeather(forceRefresh: Boolean = false): Result<WeatherSnapshot>
}
