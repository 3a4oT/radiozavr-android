package com.rovenskyi.radiozavr.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.PlayerEvent
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
import com.rovenskyi.radiozavr.domain.analytics.model.PlaySource
import com.rovenskyi.radiozavr.domain.analytics.model.StopReason
import com.rovenskyi.radiozavr.domain.network.NetworkStatus
import com.rovenskyi.radiozavr.domain.playback.PlaybackSettingsRepository
import com.rovenskyi.radiozavr.domain.radio.PlayerState
import com.rovenskyi.radiozavr.domain.usecase.ClearErrorUseCase
import com.rovenskyi.radiozavr.domain.usecase.ObserveAudioSessionIdUseCase
import com.rovenskyi.radiozavr.domain.usecase.ObserveNetworkStatusUseCase
import com.rovenskyi.radiozavr.domain.usecase.ObservePlayerErrorUseCase
import com.rovenskyi.radiozavr.domain.usecase.ObservePlayerStateUseCase
import com.rovenskyi.radiozavr.domain.usecase.ObserveVisualizerAmplitudesUseCase
import com.rovenskyi.radiozavr.domain.usecase.PlayRadioUseCase
import com.rovenskyi.radiozavr.domain.usecase.StartVisualizerCaptureUseCase
import com.rovenskyi.radiozavr.domain.usecase.StopRadioUseCase
import com.rovenskyi.radiozavr.domain.usecase.StopVisualizerCaptureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RadioPlayerUiState(
    val playerState: PlayerState = PlayerState.STOPPED,
    val networkStatus: NetworkStatus = NetworkStatus.AVAILABLE,
    val errorMessage: String? = null,
)

@HiltViewModel
class RadioPlayerViewModel @Inject constructor(
    private val playRadioUseCase: PlayRadioUseCase,
    private val stopRadioUseCase: StopRadioUseCase,
    observePlayerStateUseCase: ObservePlayerStateUseCase,
    observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
    observePlayerErrorUseCase: ObservePlayerErrorUseCase,
    private val observeAudioSessionIdUseCase: ObserveAudioSessionIdUseCase,
    observeVisualizerAmplitudesUseCase: ObserveVisualizerAmplitudesUseCase,
    private val startVisualizerCaptureUseCase: StartVisualizerCaptureUseCase,
    private val stopVisualizerCaptureUseCase: StopVisualizerCaptureUseCase,
    private val clearErrorUseCase: ClearErrorUseCase,
    playbackSettingsRepository: PlaybackSettingsRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private var hasAttemptedAutoPlay = false

    val autoPlayEnabled: StateFlow<Boolean> = playbackSettingsRepository.autoPlayEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    val uiState: StateFlow<RadioPlayerUiState> = combine(
        observePlayerStateUseCase(),
        observeNetworkStatusUseCase(),
        observePlayerErrorUseCase(),
    ) { playerState, networkStatus, errorMessage ->
        RadioPlayerUiState(
            playerState = playerState,
            networkStatus = networkStatus,
            errorMessage = errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RadioPlayerUiState(),
    )

    val visualizerAmplitudes: StateFlow<List<Float>?> =
        observeVisualizerAmplitudesUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    init {
        analyticsTracker.track(ScreenEvent.RadioPlayer)
        clearError()
        observeAudioSession()
    }

    private fun observeAudioSession() {
        viewModelScope.launch {
            observeAudioSessionIdUseCase().collect { sessionId ->
                if (sessionId != null) {
                    startVisualizerCaptureUseCase(sessionId)
                } else {
                    stopVisualizerCaptureUseCase()
                }
            }
        }
    }

    fun togglePlayStop() {
        viewModelScope.launch {
            if (uiState.value.playerState == PlayerState.PLAYING) {
                stopRadioUseCase()
                analyticsTracker.track(PlayerEvent.PlayStopped(StopReason.USER_CLICK))
            } else {
                playRadioUseCase()
                analyticsTracker.track(PlayerEvent.PlayStarted(PlaySource.USER_CLICK))
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            clearError()
            playRadioUseCase()
            analyticsTracker.track(PlayerEvent.PlayStarted(PlaySource.USER_CLICK))
        }
    }

    /**
     * Attempts auto-play on app start. Only executes once per app session.
     * The flag is always set regardless of [isEnabled] to prevent auto-play
     * from triggering if user enables the setting mid-session.
     *
     * @param isEnabled Whether auto-play setting is enabled
     * @return true if auto-play was triggered, false if skipped
     */
    fun tryAutoPlay(isEnabled: Boolean): Boolean {
        if (hasAttemptedAutoPlay) return false
        hasAttemptedAutoPlay = true

        if (!isEnabled) return false

        val currentState = uiState.value.playerState
        if (currentState == PlayerState.PLAYING || currentState == PlayerState.LOADING) {
            return false
        }

        viewModelScope.launch {
            playRadioUseCase()
            analyticsTracker.track(PlayerEvent.PlayStarted(PlaySource.AUTO_PLAY))
        }
        return true
    }

    private fun clearError() {
        viewModelScope.launch {
            clearErrorUseCase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopVisualizerCaptureUseCase()
    }
}
