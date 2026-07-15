package com.rovenskyi.radiozavr.core.network.openmeteo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponse(
    val current: CurrentWeather,
    val hourly: HourlyWeather,
) {
    @Serializable
    data class CurrentWeather(
        @SerialName("temperature_2m") val temperature2m: Double,
        @SerialName("relative_humidity_2m") val relativeHumidity2m: Int,
        @SerialName("weather_code") val weatherCode: Int,
    )

    @Serializable
    data class HourlyWeather(
        val time: List<String>,
        @SerialName("temperature_2m") val temperature2m: List<Double>,
        @SerialName("weather_code") val weatherCode: List<Int>,
    )
}
