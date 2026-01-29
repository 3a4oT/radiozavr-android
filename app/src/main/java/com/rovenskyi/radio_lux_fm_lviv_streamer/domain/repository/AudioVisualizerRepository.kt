package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

import kotlinx.coroutines.flow.Flow

interface AudioVisualizerRepository {
    fun getAmplitudes(): Flow<List<Float>?>
    fun startCapture(audioSessionId: Int): Boolean
    fun stopCapture()
}
