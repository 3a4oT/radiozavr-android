package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.PlayerState
import kotlinx.coroutines.flow.Flow

interface RadioRepository {
    fun getPlayerState(): Flow<PlayerState>
    fun getPlayerError(): Flow<String?>
    suspend fun play()
    suspend fun stop()
    suspend fun clearError()
} 