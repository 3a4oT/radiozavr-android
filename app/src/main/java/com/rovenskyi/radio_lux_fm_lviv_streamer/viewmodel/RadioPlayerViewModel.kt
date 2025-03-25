package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.repository.RadioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioPlayerViewModel @Inject constructor(
    application: Application,
    private val radioRepository: RadioRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val isPlaying: StateFlow<Boolean> get() = radioRepository.isPlaying
    val playerError: StateFlow<Boolean> get() = radioRepository.playerError
    val playerErrorMessage: StateFlow<String?> get() = radioRepository.playerErrorMessage

    val playerErrorLiveData = radioRepository.playerErrorLiveData
    val playerIsLoadingLiveData = radioRepository.playerIsLoadingLiveData
    val networkStatusLiveData = radioRepository.networkStatusLiveData

    init {
        if (!savedStateHandle.contains("isPlaying")) {
            savedStateHandle["isPlaying"] = false
        }
    }

    fun togglePlayStop() {
        viewModelScope.launch {
            radioRepository.togglePlayStop()
            saveIsPlayingState(radioRepository.isPlaying.value)
        }
    }

    private fun saveIsPlayingState(isPlaying: Boolean) {
        savedStateHandle["isPlaying"] = isPlaying
    }

    fun retry() {
        radioRepository.retry()
    }

    fun refreshState() {
        radioRepository.refreshState()
    }

    fun handlePlayerError(message: String?) {
        radioRepository.handlePlayerError(message)
    }
}