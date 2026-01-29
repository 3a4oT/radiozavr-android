package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.AudioVisualizerRepository
import javax.inject.Inject

class StartVisualizerCaptureUseCase @Inject constructor(
    private val audioVisualizerRepository: AudioVisualizerRepository,
) {
    operator fun invoke(audioSessionId: Int): Boolean =
        audioVisualizerRepository.startCapture(audioSessionId)
}
