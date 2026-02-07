package com.rovenskyi.radiozavr.domain.audio

import kotlinx.coroutines.flow.Flow

interface AudioVisualizerRepository {
    fun getAmplitudes(): Flow<List<Float>?>
    fun startCapture(audioSessionId: Int): Boolean
    fun stopCapture()

    /**
     * Restarts the visualizer capture with the last known audio session ID.
     * Use this after granting RECORD_AUDIO permission to switch from mock to real data.
     */
    fun restartCapture()
}
