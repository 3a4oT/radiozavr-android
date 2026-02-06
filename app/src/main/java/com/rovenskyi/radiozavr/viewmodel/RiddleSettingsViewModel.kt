package com.rovenskyi.radiozavr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radiozavr.core.models.riddle.RiddleAnswerMode
import com.rovenskyi.radiozavr.core.models.riddle.RiddleInterval
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
import com.rovenskyi.radiozavr.domain.analytics.event.SettingsEvent
import com.rovenskyi.radiozavr.domain.repository.PlaybackSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiddleSettingsViewModel @Inject constructor(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    init {
        analyticsTracker.track(ScreenEvent.Riddle)
    }

    val riddleInterval: StateFlow<RiddleInterval> = playbackSettingsRepository.riddleInterval
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RiddleInterval.DEFAULT,
        )

    val riddleAnswerMode: StateFlow<RiddleAnswerMode> = playbackSettingsRepository.riddleAnswerMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RiddleAnswerMode.DEFAULT,
        )

    fun setRiddleInterval(interval: RiddleInterval) {
        viewModelScope.launch {
            playbackSettingsRepository.setRiddleInterval(interval)
            analyticsTracker.track(SettingsEvent.RiddleIntervalChanged(interval))
        }
    }

    fun setRiddleAnswerMode(mode: RiddleAnswerMode) {
        viewModelScope.launch {
            playbackSettingsRepository.setRiddleAnswerMode(mode)
            analyticsTracker.track(SettingsEvent.RiddleAnswerModeChanged(mode))
        }
    }
}
