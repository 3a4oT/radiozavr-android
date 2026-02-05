package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.repository.RadioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAudioSessionIdUseCase @Inject constructor(
    private val radioRepository: RadioRepository,
) {
    operator fun invoke(): Flow<Int?> = radioRepository.getAudioSessionId()
}
