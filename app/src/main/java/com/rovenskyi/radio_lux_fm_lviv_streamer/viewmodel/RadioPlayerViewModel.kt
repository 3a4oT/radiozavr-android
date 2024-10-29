package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RadioPlayerViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _networkError = MutableStateFlow(false)
    val networkError: StateFlow<Boolean> get() = _networkError

private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application)
    .build().apply {
        // network_security_config.xml tuned to permit clear text traffic
        val luxFmStreamUri = "http://streamvideo.luxnet.ua/luxlviv/luxlviv.stream/chunklist.m3u8"
        val mediaItem = MediaItem.fromUri(luxFmStreamUri)
        setMediaItem(mediaItem)
        prepare()
    }

    fun togglePlayStop() {
        if (_isPlaying.value) {
            exoPlayer.pause()
            _isPlaying.value = false
        } else {
            viewModelScope.launch {
                try {
                    exoPlayer.prepare()
                    exoPlayer.play()
                    _isPlaying.value = true
                    _networkError.value = false
                } catch (e: IOException) {
                    _networkError.value = true
                }
            }
        }
    }

    fun retry() {
        _networkError.value = false
        togglePlayStop()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
