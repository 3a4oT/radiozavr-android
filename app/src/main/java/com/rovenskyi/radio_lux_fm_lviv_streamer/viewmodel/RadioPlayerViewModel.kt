package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.CheckNetworkService
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.NetworkErrorReceiver
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
    private val networkErrorReceiver: NetworkErrorReceiver
) : AndroidViewModel(application) {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _networkError = MutableStateFlow(false)
    val networkError: StateFlow<Boolean> get() = _networkError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    val networkErrorLiveData: LiveData<String?> get() = networkErrorReceiver.networkErrorLiveData

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
                    _networkError.value = false
                } catch (e: Exception) {
                    _networkError.value = true
                }
            }
        }
    }

    fun retry() {
        networkErrorReceiver.clearErrorMessage()
        _networkError.value = false
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
        startRadioService(RadioService.ACTION_PAUSE)
    }

    fun handleNetworkError(message: String?) {
        _errorMessage.value = message
        _networkError.value = true
    }
}
