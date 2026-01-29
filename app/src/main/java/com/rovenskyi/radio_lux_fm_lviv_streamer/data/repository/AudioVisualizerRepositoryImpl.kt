package com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository

import com.rovenskyi.radio_lux_fm_lviv_streamer.audio.AudioVisualizerCapture
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.AudioVisualizerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioVisualizerRepositoryImpl @Inject constructor() : AudioVisualizerRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var amplitudesCollectionJob: Job? = null

    private var audioVisualizerCapture: AudioVisualizerCapture? = null
    private var currentAudioSessionId: Int? = null

    private val _amplitudes = MutableStateFlow<List<Float>?>(null)

    override fun getAmplitudes(): Flow<List<Float>?> = _amplitudes.asStateFlow()

    override fun startCapture(audioSessionId: Int): Boolean {
        if (audioSessionId == currentAudioSessionId && audioVisualizerCapture != null) {
            return true
        }

        stopCapture()
        currentAudioSessionId = audioSessionId

        return try {
            val capture = AudioVisualizerCapture(audioSessionId)
            if (capture.start()) {
                audioVisualizerCapture = capture
                amplitudesCollectionJob = scope.launch {
                    capture.amplitudes.collect { amplitudes ->
                        _amplitudes.value = amplitudes
                    }
                }
                true
            } else {
                capture.release()
                false
            }
        } catch (_: Exception) {
            _amplitudes.value = null
            false
        }
    }

    override fun stopCapture() {
        amplitudesCollectionJob?.cancel()
        amplitudesCollectionJob = null
        audioVisualizerCapture?.release()
        audioVisualizerCapture = null
        currentAudioSessionId = null
        _amplitudes.value = null
    }
}
