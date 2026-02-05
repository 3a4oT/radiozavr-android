package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.model.PlayerState
import com.rovenskyi.radiozavr.domain.repository.RadioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlayerStateUseCase @Inject constructor(private val radioRepository: RadioRepository) {
    operator fun invoke(): Flow<PlayerState> = radioRepository.getPlayerState()
}
