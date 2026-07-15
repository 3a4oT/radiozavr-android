package com.rovenskyi.radiozavr.core.network.openmeteo

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Builds a ready-to-use [OpenMeteoApi], keeping Retrofit/OkHttp construction
 * encapsulated inside :core:network rather than leaking those dependencies to :app.
 */
object OpenMeteoApiFactory {

    private val json = Json { ignoreUnknownKeys = true }

    fun create(enableLogging: Boolean = false): OpenMeteoApi {
        val client = OkHttpClient.Builder()
            .apply {
                if (enableLogging) {
                    addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
                }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(OpenMeteoApi.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(OpenMeteoApi::class.java)
    }
}
