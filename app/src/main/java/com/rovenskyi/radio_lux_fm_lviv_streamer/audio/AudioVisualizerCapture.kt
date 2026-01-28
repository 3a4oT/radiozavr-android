package com.rovenskyi.radio_lux_fm_lviv_streamer.audio

import android.media.audiofx.Visualizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Captures audio visualization data from the system's audio output.
 * Uses Android's Visualizer API to get FFT data and converts it to amplitudes.
 *
 * Requires RECORD_AUDIO permission on Android 9+.
 *
 * Usage:
 * 1. Create instance with audioSessionId from ExoPlayer
 * 2. Call start() to begin capturing
 * 3. Observe amplitudes StateFlow for visualization data
 * 4. Call release() when done
 */
class AudioVisualizerCapture(
    private val audioSessionId: Int,
    private val barCount: Int = DEFAULT_BAR_COUNT
) {
    private var visualizer: Visualizer? = null

    private val _amplitudes = MutableStateFlow(List(barCount) { 0f })
    val amplitudes: StateFlow<List<Float>> = _amplitudes.asStateFlow()

    /**
     * Starts capturing audio visualization data.
     * @return true if started successfully, false if failed (e.g., no permission)
     */
    fun start(): Boolean {
        return try {
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            visualizer: Visualizer,
                            waveform: ByteArray,
                            samplingRate: Int
                        ) {
                            // Not used - we use FFT data
                        }

                        override fun onFftDataCapture(
                            visualizer: Visualizer,
                            fft: ByteArray,
                            samplingRate: Int
                        ) {
                            _amplitudes.value = computeAmplitudes(fft)
                        }
                    },
                    Visualizer.getMaxCaptureRate() / 2,
                    false,
                    true
                )
                enabled = true
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Stops capturing and releases resources.
     */
    fun release() {
        visualizer?.apply {
            enabled = false
            release()
        }
        visualizer = null
        _amplitudes.value = List(barCount) { 0f }
    }

    /**
     * Converts FFT data to amplitudes for visualization bars.
     * Uses lower frequency bands (bass/mids) which have more energy in music.
     */
    private fun computeAmplitudes(fft: ByteArray): List<Float> {
        if (fft.size < 4) return List(barCount) { 0f }

        // FFT data format: [real0, imag0, real1, imag1, ...]
        // Use only lower frequencies (bass/mids) for better visualization
        val usableBins = (fft.size / 4).coerceAtLeast(barCount * 2)
        val magnitudes = mutableListOf<Float>()

        for (i in 2 until minOf(usableBins, fft.size - 1) step 2) {
            val real = fft[i].toFloat()
            val imag = fft[i + 1].toFloat()
            val magnitude = kotlin.math.sqrt(real * real + imag * imag)
            magnitudes.add(magnitude)
        }

        if (magnitudes.isEmpty()) return List(barCount) { 0f }

        val binsPerBar = (magnitudes.size / barCount).coerceAtLeast(1)
        val result = mutableListOf<Float>()

        for (bar in 0 until barCount) {
            val startIdx = bar * binsPerBar
            val endIdx = minOf(startIdx + binsPerBar, magnitudes.size)
            val avg = if (startIdx < endIdx) {
                magnitudes.subList(startIdx, endIdx).average().toFloat()
            } else {
                0f
            }
            // Lower normalization factor + gain for visible bars
            val normalized = (avg / NORMALIZATION_FACTOR * GAIN).coerceIn(0f, 1f)
            result.add(normalized)
        }

        return result
    }

    companion object {
        private const val DEFAULT_BAR_COUNT = 4
        private const val NORMALIZATION_FACTOR = 30f
        private const val GAIN = 2f
    }
}
