package com.rovenskyi.radiozavr.di

import com.rovenskyi.radiozavr.BuildConfig
import com.rovenskyi.radiozavr.core.network.openmeteo.OpenMeteoApi
import com.rovenskyi.radiozavr.core.network.openmeteo.OpenMeteoApiFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOpenMeteoApi(): OpenMeteoApi = OpenMeteoApiFactory.create(enableLogging = BuildConfig.DEBUG)
}
