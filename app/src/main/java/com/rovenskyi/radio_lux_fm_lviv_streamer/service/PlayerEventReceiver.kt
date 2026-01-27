package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerEventReceiver @Inject constructor() {

    private val _playerError = MutableStateFlow<String?>(null)
    val playerError: StateFlow<String?> = _playerError.asStateFlow()

    private val _playerIsLoading = MutableStateFlow(false)
    val playerIsLoading: StateFlow<Boolean> = _playerIsLoading.asStateFlow()

    // New live data to handle playing state changes from any source (e.g., notification)
    private val _playerState = MutableStateFlow(false)
    val playerState: StateFlow<Boolean> = _playerState.asStateFlow()

    fun postPlayerError(message: String?) {
        _playerError.value = message
    }

    fun clearPlayerErrorMessage() {
        _playerError.value = null
    }

    fun postPlayerIsLoading(isLoading: Boolean) {
        _playerIsLoading.value = isLoading
    }

    // New function to post player state changes
    fun postPlayerState(isPlaying: Boolean) {
        _playerState.value = isPlaying
    }
}

