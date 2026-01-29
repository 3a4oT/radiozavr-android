package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlatformRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlaybackSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackSettingsViewModel @Inject constructor(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    platformRepository: PlatformRepository,
) : ViewModel() {

    val isTv: Boolean = platformRepository.isTv

    val autoPlayEnabled: StateFlow<Boolean> = playbackSettingsRepository.autoPlayEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = isTv,
        )

    val autoStopOnBackgroundEnabled: StateFlow<Boolean> = playbackSettingsRepository.autoStopOnBackgroundEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = isTv,
        )

    fun setAutoPlayEnabled(enabled: Boolean) {
        viewModelScope.launch {
            playbackSettingsRepository.setAutoPlayEnabled(enabled)
        }
    }

    fun setAutoStopOnBackgroundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            playbackSettingsRepository.setAutoStopOnBackgroundEnabled(enabled)
        }
    }
}
