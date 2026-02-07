package com.rovenskyi.radiozavr.domain.radio

import kotlinx.coroutines.flow.Flow

interface RadioRepository {
    fun getPlayerState(): Flow<PlayerState>
    fun getPlayerError(): Flow<String?>
    fun getAudioSessionId(): Flow<Int?>
    suspend fun play()
    suspend fun stop()
    suspend fun clearError()
}
