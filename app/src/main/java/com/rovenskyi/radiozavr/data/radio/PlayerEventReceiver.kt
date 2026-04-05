package com.rovenskyi.radiozavr.data.radio

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
        // Do NOT touch _playerState/_playerIsLoading: they reflect actual ExoPlayer state
        // and are already false after an error. Resetting them here would cause a STOPPED
        // flash if called while ExoPlayer is playing (e.g. on ViewModel re-creation).
        hasPlayedSuccessfully = false
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
     * Buffer ready (STATE_READY):
     * - Immediately hides spinner
     * - Marks that buffer filled (for future rebuffer detection)
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
            // Buffer filled (STATE_READY) - immediately hide spinner
            bufferingJob?.cancel()
            bufferingJob = null
            _playerIsLoading.value = false

            // Mark that buffer filled - regardless of playerState
            // (STATE_READY callback may arrive before onIsPlayingChanged)
            hasPlayedSuccessfully = true
        }
    }

    /**
     * Posts player state (playing/stopped).
     *
     * Called from onIsPlayingChanged - only when player.isPlaying changes.
     * Note: isPlaying=true requires STATE_READY, so hasPlayedSuccessfully
     * should already be true by the time this is called with isPlaying=true.
     */
    fun postPlayerState(isPlaying: Boolean) {
        if (!isPlaying) {
            // Stopped - reset state
            bufferingJob?.cancel()
            bufferingJob = null
            hasPlayedSuccessfully = false
            _playerIsLoading.value = false
            _playerState.value = false
        } else {
            // Playing - just update state
            // Loading state is managed by postPlayerIsLoading (from onPlaybackStateChanged)
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
