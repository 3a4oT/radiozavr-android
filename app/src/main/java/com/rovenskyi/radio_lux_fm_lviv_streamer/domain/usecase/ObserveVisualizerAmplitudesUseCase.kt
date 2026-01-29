package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.AudioVisualizerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveVisualizerAmplitudesUseCase @Inject constructor(
    private val audioVisualizerRepository: AudioVisualizerRepository
) {
    operator fun invoke(): Flow<List<Float>?> = audioVisualizerRepository.getAmplitudes()
}
