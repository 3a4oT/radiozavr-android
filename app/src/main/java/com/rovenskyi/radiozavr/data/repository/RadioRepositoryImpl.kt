package com.rovenskyi.radiozavr.data.repository

import com.rovenskyi.radiozavr.data.source.RadioServiceDataSource
import com.rovenskyi.radiozavr.domain.model.PlayerState
import com.rovenskyi.radiozavr.domain.repository.RadioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class RadioRepositoryImpl @Inject constructor(
    private val radioServiceDataSource: RadioServiceDataSource,
) : RadioRepository {

    override fun getPlayerState(): Flow<PlayerState> {
        return combine(
            radioServiceDataSource.getPlayerState(),
            radioServiceDataSource.getPlayerIsLoading(),
            radioServiceDataSource.getPlayerError(),
        ) { isPlaying, isLoading, error ->
            when {
                error != null -> PlayerState.ERROR
                isLoading -> PlayerState.LOADING
                isPlaying -> PlayerState.PLAYING
                else -> PlayerState.STOPPED
            }
        }
    }

    override fun getPlayerError(): Flow<String?> {
        return radioServiceDataSource.getPlayerError()
    }

    override fun getAudioSessionId(): Flow<Int?> {
        return radioServiceDataSource.getAudioSessionId()
    }

    override suspend fun play() {
        radioServiceDataSource.play()
    }

    override suspend fun stop() {
        radioServiceDataSource.stop()
    }

    override suspend fun clearError() {
        radioServiceDataSource.clearError()
    }
}
