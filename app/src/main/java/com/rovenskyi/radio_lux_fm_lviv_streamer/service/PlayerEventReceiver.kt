package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton that receives and debounces player events.
 *
 * Buffering state uses debounce to avoid UI flickering on unstable networks:
 * - Shows buffering only after [BUFFERING_DEBOUNCE_MS] of continuous buffering
 * - Hides buffering immediately when ready (buffer filled)
 */
@Singleton
class PlayerEventReceiver @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _playerError = MutableStateFlow<String?>(null)
    val playerError: StateFlow<String?> = _playerError.asStateFlow()

    private val _playerIsLoading = MutableStateFlow(false)
    val playerIsLoading: StateFlow<Boolean> = _playerIsLoading.asStateFlow()

    private val _playerState = MutableStateFlow(false)
    val playerState: StateFlow<Boolean> = _playerState.asStateFlow()

    private var bufferingJob: Job? = null
    private var wasPlayingBeforeBuffer = false

    fun postPlayerError(message: String?) {
        _playerError.value = message
    }

    fun clearPlayerErrorMessage() {
        _playerError.value = null
    }

    /**
     * Posts loading state with debounce for buffering.
     *
     * When buffering starts:
     * - Waits [BUFFERING_DEBOUNCE_MS] before showing spinner
     * - If buffer fills before timeout, spinner never shows
     *
     * When buffering ends:
     * - Immediately hides spinner (no delay)
     */
    fun postPlayerIsLoading(isLoading: Boolean) {
        if (isLoading) {
            // Remember if we were playing before buffering started
            if (_playerState.value) {
                wasPlayingBeforeBuffer = true
            }

            // Cancel any pending job and start new debounced one
            bufferingJob?.cancel()
            bufferingJob = scope.launch {
                delay(BUFFERING_DEBOUNCE_MS)
                _playerIsLoading.value = true
            }
        } else {
            // Buffer filled - immediately hide spinner
            bufferingJob?.cancel()
            bufferingJob = null
            _playerIsLoading.value = false
            wasPlayingBeforeBuffer = false
        }
    }

    /**
     * Posts player state (playing/stopped).
     */
    fun postPlayerState(isPlaying: Boolean) {
        _playerState.value = isPlaying

        // If stopped, cancel any pending buffering indication
        if (!isPlaying) {
            bufferingJob?.cancel()
            bufferingJob = null
            wasPlayingBeforeBuffer = false
        }
    }

    companion object {
        /**
         * Delay before showing buffering spinner.
         * Short buffers (< 500ms) won't trigger visual feedback.
         */
        private const val BUFFERING_DEBOUNCE_MS = 500L
    }
}

