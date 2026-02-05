package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.repository.AudioVisualizerRepository
import javax.inject.Inject

class StopVisualizerCaptureUseCase @Inject constructor(
    private val audioVisualizerRepository: AudioVisualizerRepository,
) {
    operator fun invoke() = audioVisualizerRepository.stopCapture()
}
