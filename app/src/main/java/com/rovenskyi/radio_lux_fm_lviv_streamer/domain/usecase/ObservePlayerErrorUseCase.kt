package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RadioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlayerErrorUseCase @Inject constructor(private val radioRepository: RadioRepository) {
    operator fun invoke(): Flow<String?> = radioRepository.getPlayerError()
}
