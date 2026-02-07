package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.audio.AudioVisualizerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveVisualizerAmplitudesUseCase @Inject constructor(
    private val audioVisualizerRepository: AudioVisualizerRepository,
) {
    operator fun invoke(): Flow<List<Float>?> = audioVisualizerRepository.getAmplitudes()
}
