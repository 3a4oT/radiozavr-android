package com.rovenskyi.radiozavr.data.audio

import com.rovenskyi.radiozavr.data.audio.AudioVisualizerCapture
import com.rovenskyi.radiozavr.domain.audio.AudioVisualizerRepository
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
    private var lastRequestedAudioSessionId: Int? = null

    private val _amplitudes = MutableStateFlow<List<Float>?>(null)

    override fun getAmplitudes(): Flow<List<Float>?> = _amplitudes.asStateFlow()

    override fun startCapture(audioSessionId: Int): Boolean {
        // Always store the session ID for potential restart
        lastRequestedAudioSessionId = audioSessionId

        if (audioSessionId == currentAudioSessionId && audioVisualizerCapture != null) {
            return true
        }

        stopCaptureInternal()
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
        stopCaptureInternal()
        lastRequestedAudioSessionId = null
    }

    override fun restartCapture() {
        val sessionId = lastRequestedAudioSessionId ?: return
        stopCaptureInternal()
        startCapture(sessionId)
    }

    private fun stopCaptureInternal() {
        amplitudesCollectionJob?.cancel()
        amplitudesCollectionJob = null
        audioVisualizerCapture?.release()
        audioVisualizerCapture = null
        currentAudioSessionId = null
        _amplitudes.value = null
    }
}
