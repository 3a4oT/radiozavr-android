package com.rovenskyi.radiozavr.data.widget.weather

import com.rovenskyi.radiozavr.core.models.weather.HourlyForecast
import com.rovenskyi.radiozavr.core.models.weather.WeatherCondition
import com.rovenskyi.radiozavr.core.models.weather.WeatherSnapshot
import com.rovenskyi.radiozavr.core.network.openmeteo.OpenMeteoApi
import com.rovenskyi.radiozavr.core.network.openmeteo.OpenMeteoResponse
import com.rovenskyi.radiozavr.domain.widget.weather.WeatherRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

private const val LVIV_LATITUDE = 49.8397
private const val LVIV_LONGITUDE = 24.0297
private const val CACHE_TTL_MS = 30 * 60 * 1000L

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val openMeteoApi: OpenMeteoApi,
) : WeatherRepository {

    private val mutex = Mutex()
    private var cached: WeatherSnapshot? = null

    override suspend fun getCurrentWeather(forceRefresh: Boolean): Result<WeatherSnapshot> = mutex.withLock {
        val now = System.currentTimeMillis()
        val fresh = cached?.takeIf { !forceRefresh && now - it.fetchedAtMillis < CACHE_TTL_MS }
        if (fresh != null) return@withLock Result.success(fresh)

        runCatching {
            val response = openMeteoApi.getCurrentWeather(latitude = LVIV_LATITUDE, longitude = LVIV_LONGITUDE)
            WeatherSnapshot(
                temperatureCelsius = response.current.temperature2m,
                humidityPercent = response.current.relativeHumidity2m,
                condition = response.current.weatherCode.toWeatherCondition(),
                hourlyForecast = response.hourly.toUpcomingForecast(),
                fetchedAtMillis = now,
            )
        }.onSuccess { cached = it }
    }
}

/**
 * Drops the current hour (index 0, per forecast_hours semantics) and keeps the next 3.
 */
private fun OpenMeteoResponse.HourlyWeather.toUpcomingForecast(): List<HourlyForecast> =
    time.indices.drop(1).map { index ->
        HourlyForecast(
            hourOffset = index,
            temperatureCelsius = temperature2m[index],
            condition = weatherCode[index].toWeatherCondition(),
        )
    }

/**
 * Maps WMO weather interpretation codes (used by Open-Meteo) to a simplified condition.
 * https://open-meteo.com/en/docs#weathervariables
 */
private fun Int.toWeatherCondition(): WeatherCondition = when (this) {
    0 -> WeatherCondition.CLEAR
    1, 2 -> WeatherCondition.PARTLY_CLOUDY
    3 -> WeatherCondition.CLOUDY
    45, 48 -> WeatherCondition.FOG
    51, 53, 55, 56, 57 -> WeatherCondition.DRIZZLE
    61, 63, 65, 66, 67, 80, 81, 82 -> WeatherCondition.RAIN
    71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOW
    95, 96, 99 -> WeatherCondition.THUNDERSTORM
    else -> WeatherCondition.UNKNOWN
}
