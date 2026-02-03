package com.rovenskyi.radio_lux_fm_lviv_streamer.di

import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.AudioVisualizerRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.LanguageRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.NetworkRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.PermissionRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.PlatformRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.PlaybackSettingsRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.RadioRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.RiddleRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.ThemeRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.AudioVisualizerRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.LanguageRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.NetworkRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PermissionRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlatformRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlaybackSettingsRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RadioRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RiddleRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.ThemeRepository
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
