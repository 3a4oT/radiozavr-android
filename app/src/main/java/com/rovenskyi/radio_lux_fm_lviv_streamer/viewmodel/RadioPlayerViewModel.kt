package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.CheckNetworkService
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.PlayerEventReceiver
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.RadioService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioPlayerViewModel @Inject constructor(
    application: Application,
    private val checkNetworkService: CheckNetworkService,
    private val playerEventReceiver: PlayerEventReceiver
) : AndroidViewModel(application) {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _playerError = MutableStateFlow(false)
    val playerError: StateFlow<Boolean> get() = _playerError

    private val _playerErrorMessage = MutableStateFlow<String?>(null)
    val playerErrorMessage: StateFlow<String?> get() = _playerErrorMessage

    val playerErrorLiveData: LiveData<String?> get() = playerEventReceiver.playerErrorLiveData
    val playerIsLoadingLiveData: LiveData<Boolean> get() = playerEventReceiver.playerIsLoadingLiveData
    val networkStatusLiveData: LiveData<Boolean> get() = checkNetworkService.networkStatusLiveData

    private val appContext = application.applicationContext

    fun togglePlayStop() {
        if (_isPlaying.value) {
            stopRadioService()
            _isPlaying.value = false
        } else {
            viewModelScope.launch {
                try {
                    checkNetworkService.checkNetworkConnection()
                    startRadioService(RadioService.ACTION_PLAY)
                    _isPlaying.value = true
                    _playerError.value = false
                } catch (e: Exception) {
                    _playerError.value = true
                }
            }
        }
    }

    fun retry() {
        playerEventReceiver.clearPlayerErrorMessage()
        _playerError.value = false
        togglePlayStop()
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
}
