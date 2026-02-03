package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.AnalyticsTracker
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event.RiddleEvent
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlaybackSettingsRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RiddleRepository
import com.rovenskyi.radiolux.core.models.riddle.Riddle
import com.rovenskyi.radiolux.core.models.riddle.RiddleInterval
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for RiddleWidget.
 * Manages riddle loading and rotation based on user-configured interval.
 */
@HiltViewModel
class RiddleWidgetViewModel @Inject constructor(
    private val riddleRepository: RiddleRepository,
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _currentRiddle = MutableStateFlow<Riddle?>(null)
    val currentRiddle: StateFlow<Riddle?> = _currentRiddle.asStateFlow()

    private val _riddleIndex = MutableStateFlow(0)
    val riddleIndex: StateFlow<Int> = _riddleIndex.asStateFlow()

    private var rotationJob: Job? = null

    init {
        loadNextRiddle()
        observeIntervalChanges()
    }

    private fun loadNextRiddle() {
        viewModelScope.launch {
            _currentRiddle.value = riddleRepository.getNextUniqueRiddle()
            _riddleIndex.value++
        }
    }

    private fun observeIntervalChanges() {
        viewModelScope.launch {
            playbackSettingsRepository.riddleInterval.collect { interval ->
                restartRotation(interval)
            }
        }
    }

    private fun restartRotation(interval: RiddleInterval) {
        rotationJob?.cancel()
        rotationJob = viewModelScope.launch {
            while (isActive) {
                delay(interval.milliseconds)
                loadNextRiddle()
            }
        }
    }

    fun trackAnswerRevealed(isTv: Boolean) {
        analyticsTracker.track(RiddleEvent.AnswerRevealed(isTv))
    }
}
