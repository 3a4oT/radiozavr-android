package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.repository.RadioRepository
import javax.inject.Inject

class PlayRadioUseCase @Inject constructor(private val radioRepository: RadioRepository) {
    suspend operator fun invoke() = radioRepository.play()
}
