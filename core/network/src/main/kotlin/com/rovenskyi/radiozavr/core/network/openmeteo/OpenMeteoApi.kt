package com.rovenskyi.radiozavr.core.network.openmeteo

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Client for the free Open-Meteo forecast API (no API key required).
 * https://open-meteo.com/en/docs
 */
interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,weather_code",
        @Query("forecast_hours") forecastHours: Int = 4,
        @Query("timezone") timezone: String = "auto",
    ): OpenMeteoResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"
    }
}
