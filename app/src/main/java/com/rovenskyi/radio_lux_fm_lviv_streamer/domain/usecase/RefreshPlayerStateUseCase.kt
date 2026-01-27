package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RadioRepository
import javax.inject.Inject

class RefreshPlayerStateUseCase @Inject constructor(private val radioRepository: RadioRepository) {
    suspend operator fun invoke() = radioRepository.refreshState()
} 