package com.rovenskyi.radio_lux_fm_lviv_streamer.repository

import android.app.Application
import androidx.lifecycle.Observer
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.CheckNetworkService
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.PlayerEventReceiver
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.RadioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class RadioRepository @Inject constructor(
    private val checkNetworkService: CheckNetworkService,
    private val playerEventReceiver: PlayerEventReceiver,
    private val appContext: Application
) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _playerError = MutableStateFlow(false)
    val playerError: StateFlow<Boolean> get() = _playerError

    private val _playerErrorMessage = MutableStateFlow<String?>(null)
    val playerErrorMessage: StateFlow<String?> get() = _playerErrorMessage

    val playerErrorLiveData = playerEventReceiver.playerErrorLiveData
    val playerIsLoadingLiveData = playerEventReceiver.playerIsLoadingLiveData
    val networkStatusLiveData = checkNetworkService.networkStatusLiveData

    init {
        // Observe player state changes from notification or other sources
        playerEventReceiver.playerStateLiveData.observeForever(Observer { playing ->
            _isPlaying.value = playing ?: false
        })
    }

    fun togglePlayStop() {
        CoroutineScope(Dispatchers.Main).launch {
            if (_isPlaying.value) {
                stopRadioService()
                _isPlaying.value = false
            } else {
                try {
                    checkNetworkService.checkNetworkConnection()
                    startRadioService(RadioService.ACTION_PLAY)
                    _isPlaying.value = true
                    _playerError.value = false
                } catch (_: Exception) {
                    _playerError.value = true
                }
            }
        }
    }

    private fun startRadioService(action: String) {
        val intent = when (action) {
            RadioService.ACTION_PLAY -> RadioService.createPlayIntent(appContext)
            RadioService.ACTION_PAUSE -> RadioService.createPauseIntent(appContext)
            RadioService.ACTION_STOP -> RadioService.createStopIntent(appContext)
            else -> return
        }
        appContext.startService(intent)
    }

    private fun stopRadioService() {
        startRadioService(RadioService.ACTION_STOP)
    }

    fun handlePlayerError(message: String?) {
        _playerErrorMessage.value = message
        _playerError.value = true
    }

    fun retry() {
        playerEventReceiver.clearPlayerErrorMessage()
        _playerError.value = false
        CoroutineScope(Dispatchers.Main).launch {
            togglePlayStop()
        }
    }

    // Refresh the playing state when app returns to foreground.
    fun refreshState() {
        CoroutineScope(Dispatchers.Main).launch {
            val isServicePlaying = RadioService.isServicePlaying()
            _isPlaying.value = isServicePlaying
        }
    }
}