package com.rovenskyi.radio_lux_fm_lviv_streamer.di

import com.rovenskyi.radio_lux_fm_lviv_streamer.data.analytics.FirebaseAnalyticsTracker
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.AnalyticsTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(
        firebaseAnalyticsTracker: FirebaseAnalyticsTracker,
    ): AnalyticsTracker
}
