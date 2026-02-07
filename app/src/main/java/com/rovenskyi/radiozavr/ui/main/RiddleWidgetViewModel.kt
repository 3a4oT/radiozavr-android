package com.rovenskyi.radiozavr.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radiozavr.core.models.riddle.Riddle
import com.rovenskyi.radiozavr.core.models.riddle.RiddleAnswerMode
import com.rovenskyi.radiozavr.core.models.riddle.RiddleInterval
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.RiddleEvent
import com.rovenskyi.radiozavr.domain.playback.PlaybackSettingsRepository
import com.rovenskyi.radiozavr.data.riddle.RiddleRotationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * UI state for RiddleWidget.
 */
data class RiddleUiState(
    val riddle: Riddle? = null,
    val riddleStartTime: Long = System.currentTimeMillis(),
    val interval: RiddleInterval = RiddleInterval.DEFAULT,
    val answerMode: RiddleAnswerMode = RiddleAnswerMode.DEFAULT,
)

/**
 * ViewModel for RiddleWidget.
 *
 * Following architecture guidelines:
 * - Uses combine() + stateIn() for UI state composition
 * - Delegates rotation logic to RiddleRotationService
 * - Only handles UI-specific concerns (analytics)
 */
@HiltViewModel
class RiddleWidgetViewModel @Inject constructor(
    private val riddleRotationService: RiddleRotationService,
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    /**
     * Combined UI state from service and settings.
     * Uses WhileSubscribed(5_000) per architecture guidelines.
     */
    val uiState: StateFlow<RiddleUiState> = combine(
        riddleRotationService.rotationState,
        playbackSettingsRepository.riddleInterval,
        playbackSettingsRepository.riddleAnswerMode,
    ) { rotationState, interval, answerMode ->
        RiddleUiState(
            riddle = rotationState.riddle,
            riddleStartTime = rotationState.startTimeMillis,
            interval = interval,
            answerMode = answerMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RiddleUiState(),
    )

    init {
        // Observe interval changes and update service
        playbackSettingsRepository.riddleInterval
            .onEach { interval -> riddleRotationService.setInterval(interval) }
            .launchIn(viewModelScope)
    }

    fun trackAnswerRevealed(isTv: Boolean, isAutomatic: Boolean) {
        analyticsTracker.track(RiddleEvent.AnswerRevealed(isTv, isAutomatic))
    }
}
