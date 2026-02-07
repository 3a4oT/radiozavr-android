package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.radio.RadioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlayerErrorUseCase @Inject constructor(private val radioRepository: RadioRepository) {
    operator fun invoke(): Flow<String?> = radioRepository.getPlayerError()
}
