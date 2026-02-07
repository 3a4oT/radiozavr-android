package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.audio.AudioVisualizerRepository
import javax.inject.Inject

class StopVisualizerCaptureUseCase @Inject constructor(
    private val audioVisualizerRepository: AudioVisualizerRepository,
) {
    operator fun invoke() = audioVisualizerRepository.stopCapture()
}
