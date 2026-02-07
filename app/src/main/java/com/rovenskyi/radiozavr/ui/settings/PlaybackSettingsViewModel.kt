package com.rovenskyi.radiozavr.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
import com.rovenskyi.radiozavr.domain.analytics.event.SettingsEvent
import com.rovenskyi.radiozavr.domain.platform.PlatformRepository
import com.rovenskyi.radiozavr.domain.playback.PlaybackSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackSettingsViewModel @Inject constructor(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    private val analyticsTracker: AnalyticsTracker,
    platformRepository: PlatformRepository,
) : ViewModel() {

    val isTv: Boolean = platformRepository.isTv

    init {
        analyticsTracker.track(ScreenEvent.Playback)
    }

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
            analyticsTracker.track(SettingsEvent.AutoPlayToggled(enabled))
        }
    }

    fun setAutoStopOnBackgroundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            playbackSettingsRepository.setAutoStopOnBackgroundEnabled(enabled)
            analyticsTracker.track(SettingsEvent.AutoStopToggled(enabled))
        }
    }
}
