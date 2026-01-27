package com.rovenskyi.radio_lux_fm_lviv_streamer.di

import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.NetworkRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository.RadioRepositoryImpl
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.NetworkRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RadioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindRadioRepository(
        radioRepositoryImpl: RadioRepositoryImpl
    ): RadioRepository

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        networkRepositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository
} 