package com.rovenskyi.radiozavr.di

import com.rovenskyi.radiozavr.data.repository.AudioVisualizerRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.LanguageRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.NetworkRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.PermissionRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.PlatformRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.PlaybackSettingsRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.RadioRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.RiddleRepositoryImpl
import com.rovenskyi.radiozavr.data.repository.ThemeRepositoryImpl
import com.rovenskyi.radiozavr.domain.repository.AudioVisualizerRepository
import com.rovenskyi.radiozavr.domain.repository.LanguageRepository
import com.rovenskyi.radiozavr.domain.repository.NetworkRepository
import com.rovenskyi.radiozavr.domain.repository.PermissionRepository
import com.rovenskyi.radiozavr.domain.repository.PlatformRepository
import com.rovenskyi.radiozavr.domain.repository.PlaybackSettingsRepository
import com.rovenskyi.radiozavr.domain.repository.RadioRepository
import com.rovenskyi.radiozavr.domain.repository.RiddleRepository
import com.rovenskyi.radiozavr.domain.repository.ThemeRepository
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
}
