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
 * Buffering state strategy:
 * - Cold start (first play): show spinner immediately (no buffer yet)
 * - Rebuffering (was playing): debounce [REBUFFER_DEBOUNCE_MS] to avoid flickering
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

    private val _audioSessionId = MutableStateFlow<Int?>(null)
    val audioSessionId: StateFlow<Int?> = _audioSessionId.asStateFlow()

    private var bufferingJob: Job? = null
    private var hasPlayedSuccessfully = false

    fun postPlayerError(message: String?) {
        _playerError.value = message
    }

    fun clearPlayerErrorMessage() {
        _playerError.value = null
        // Reset for retry - next play should show loading from clean state
        hasPlayedSuccessfully = false
        _playerState.value = false
        _playerIsLoading.value = false
    }

    fun postAudioSessionId(sessionId: Int?) {
        _audioSessionId.value = sessionId
    }

    /**
     * Posts loading state with smart debounce.
     *
     * Cold start (never played yet):
     * - Shows spinner immediately
     *
     * Rebuffering (already played successfully):
     * - Waits [REBUFFER_DEBOUNCE_MS] before showing spinner
     * - Prevents flickering on unstable networks
     *
     * Buffer ready:
     * - Immediately hides spinner
     * - Marks that playback succeeded (for future rebuffer detection)
     */
    fun postPlayerIsLoading(isLoading: Boolean) {
        if (isLoading) {
            bufferingJob?.cancel()

            if (hasPlayedSuccessfully) {
                // Rebuffering - use debounce to avoid flickering
                bufferingJob = scope.launch {
                    delay(REBUFFER_DEBOUNCE_MS)
                    _playerIsLoading.value = true
                }
            } else {
                // Cold start - show spinner immediately
                _playerIsLoading.value = true
            }
        } else {
            // Buffer filled - immediately hide spinner
            bufferingJob?.cancel()
            bufferingJob = null
            _playerIsLoading.value = false

            // Mark that we've played successfully (buffer was filled at least once)
            if (_playerState.value) {
                hasPlayedSuccessfully = true
            }
        }
    }

    /**
     * Posts player state (playing/stopped).
     *
     * On cold start (play pressed, never played yet):
     * - Sets loading BEFORE playing state to avoid flash of stop button
     */
    fun postPlayerState(isPlaying: Boolean) {
        if (!isPlaying) {
            // Stopped - reset state
            bufferingJob?.cancel()
            bufferingJob = null
            hasPlayedSuccessfully = false
            _playerIsLoading.value = false
            _playerState.value = false
        } else if (!hasPlayedSuccessfully) {
            // Cold start - set loading FIRST to avoid flash of stop button
            // Order matters: combine() emits after each change
            _playerIsLoading.value = true
            _playerState.value = true
        } else {
            // Already played successfully, just update state
            _playerState.value = true
        }
    }

    companion object {
        /**
         * Delay before showing rebuffering spinner.
         * Only applies when playback was already successful.
         * Short rebuffers (< 500ms) won't trigger visual feedback.
         */
        private const val REBUFFER_DEBOUNCE_MS = 500L
    }
}
