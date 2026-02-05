package com.rovenskyi.radiozavr.di

import com.rovenskyi.radiozavr.data.analytics.FirebaseAnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
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
