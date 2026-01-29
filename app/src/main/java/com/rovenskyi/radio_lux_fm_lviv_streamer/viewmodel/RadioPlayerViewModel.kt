package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.PlayerState
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.ClearErrorUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.ObserveAudioSessionIdUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.ObserveNetworkStatusUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.ObservePlayerErrorUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.ObservePlayerStateUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.ObserveVisualizerAmplitudesUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.PlayRadioUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.StartVisualizerCaptureUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.StopRadioUseCase
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.StopVisualizerCaptureUseCase
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
) : ViewModel() {

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
            } else {
                playRadioUseCase()
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            clearError()
            playRadioUseCase()
        }
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
