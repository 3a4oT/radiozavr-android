package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.AudioVisualizerRepository
import javax.inject.Inject

class StopVisualizerCaptureUseCase @Inject constructor(
    private val audioVisualizerRepository: AudioVisualizerRepository
) {
    operator fun invoke() = audioVisualizerRepository.stopCapture()
}
