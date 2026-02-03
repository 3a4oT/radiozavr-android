package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.RiddleRepository
import com.rovenskyi.radiolux.core.models.riddle.Riddle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ROTATION_INTERVAL_MS = 20_000L

/**
 * ViewModel for RotatingWidget.
 * Manages riddle loading and rotation on 20-second intervals.
 */
@HiltViewModel
class RotatingWidgetViewModel @Inject constructor(
    private val riddleRepository: RiddleRepository,
) : ViewModel() {

    private val _currentRiddle = MutableStateFlow<Riddle?>(null)
    val currentRiddle: StateFlow<Riddle?> = _currentRiddle.asStateFlow()

    private val _riddleIndex = MutableStateFlow(0)
    val riddleIndex: StateFlow<Int> = _riddleIndex.asStateFlow()

    init {
        loadNextRiddle()
        startRotation()
    }

    private fun loadNextRiddle() {
        viewModelScope.launch {
            _currentRiddle.value = riddleRepository.getRandomRiddle()
            _riddleIndex.value++
        }
    }

    private fun startRotation() {
        viewModelScope.launch {
            while (isActive) {
                delay(ROTATION_INTERVAL_MS)
                loadNextRiddle()
            }
        }
    }
}
