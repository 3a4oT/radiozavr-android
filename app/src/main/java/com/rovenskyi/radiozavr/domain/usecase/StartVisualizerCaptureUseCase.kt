package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.repository.AudioVisualizerRepository
import javax.inject.Inject

class StartVisualizerCaptureUseCase @Inject constructor(
    private val audioVisualizerRepository: AudioVisualizerRepository,
) {
    operator fun invoke(audioSessionId: Int): Boolean =
        audioVisualizerRepository.startCapture(audioSessionId)
}
