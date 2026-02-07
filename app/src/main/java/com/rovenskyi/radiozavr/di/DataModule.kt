package com.rovenskyi.radiozavr.di

import com.rovenskyi.radiozavr.data.audio.AudioVisualizerRepositoryImpl
import com.rovenskyi.radiozavr.data.language.LanguageRepositoryImpl
import com.rovenskyi.radiozavr.data.network.NetworkRepositoryImpl
import com.rovenskyi.radiozavr.data.permission.PermissionRepositoryImpl
import com.rovenskyi.radiozavr.data.platform.PlatformRepositoryImpl
import com.rovenskyi.radiozavr.data.playback.PlaybackSettingsRepositoryImpl
import com.rovenskyi.radiozavr.data.radio.RadioRepositoryImpl
import com.rovenskyi.radiozavr.data.theme.ThemeRepositoryImpl
import com.rovenskyi.radiozavr.data.widget.riddle.RiddleRepositoryImpl
import com.rovenskyi.radiozavr.data.widgetscheduler.LocalWidgetConfigSource
import com.rovenskyi.radiozavr.data.widgetscheduler.WidgetConfigRepositoryImpl
import com.rovenskyi.radiozavr.domain.audio.AudioVisualizerRepository
import com.rovenskyi.radiozavr.domain.language.LanguageRepository
import com.rovenskyi.radiozavr.domain.network.NetworkRepository
import com.rovenskyi.radiozavr.domain.permission.PermissionRepository
import com.rovenskyi.radiozavr.domain.platform.PlatformRepository
import com.rovenskyi.radiozavr.domain.playback.PlaybackSettingsRepository
import com.rovenskyi.radiozavr.domain.radio.RadioRepository
import com.rovenskyi.radiozavr.domain.theme.ThemeRepository
import com.rovenskyi.radiozavr.domain.widget.riddle.RiddleRepository
import com.rovenskyi.radiozavr.domain.widgetscheduler.WidgetConfigRepository
import com.rovenskyi.radiozavr.domain.widgetscheduler.WidgetConfigSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindRadioRepository(
        radioRepositoryImpl: RadioRepositoryImpl,
    ): RadioRepository

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        networkRepositoryImpl: NetworkRepositoryImpl,
    ): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl,
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        languageRepositoryImpl: LanguageRepositoryImpl,
    ): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindAudioVisualizerRepository(
        audioVisualizerRepositoryImpl: AudioVisualizerRepositoryImpl,
    ): AudioVisualizerRepository

    @Binds
    @Singleton
    abstract fun bindPlatformRepository(
        platformRepositoryImpl: PlatformRepositoryImpl,
    ): PlatformRepository

    @Binds
    @Singleton
    abstract fun bindPlaybackSettingsRepository(
        playbackSettingsRepositoryImpl: PlaybackSettingsRepositoryImpl,
    ): PlaybackSettingsRepository

    @Binds
    @Singleton
    abstract fun bindPermissionRepository(
        permissionRepositoryImpl: PermissionRepositoryImpl,
    ): PermissionRepository

    @Binds
    @Singleton
    abstract fun bindRiddleRepository(
        riddleRepositoryImpl: RiddleRepositoryImpl,
    ): RiddleRepository

    @Binds
    @Singleton
    abstract fun bindWidgetConfigSource(
        localWidgetConfigSource: LocalWidgetConfigSource,
    ): WidgetConfigSource

    @Binds
    @Singleton
    abstract fun bindWidgetConfigRepository(
        widgetConfigRepositoryImpl: WidgetConfigRepositoryImpl,
    ): WidgetConfigRepository
}
