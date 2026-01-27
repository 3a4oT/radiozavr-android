package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.PlayerState
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RadioPlayerUiState(
    val playerState: PlayerState = PlayerState.STOPPED,
    val networkStatus: NetworkStatus = NetworkStatus.AVAILABLE,
    val errorMessage: String? = null
)

@HiltViewModel
class RadioPlayerViewModel @Inject constructor(
    private val playRadioUseCase: PlayRadioUseCase,
    private val stopRadioUseCase: StopRadioUseCase,
    private val observePlayerStateUseCase: ObservePlayerStateUseCase,
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
    private val observePlayerErrorUseCase: ObservePlayerErrorUseCase,
    private val refreshPlayerStateUseCase: RefreshPlayerStateUseCase,
    private val clearErrorUseCase: ClearErrorUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RadioPlayerUiState())
    val uiState: StateFlow<RadioPlayerUiState> = _uiState.asStateFlow()

    init {
        clearError()
        observePlayerState()
        observeNetworkStatus()
        observePlayerError()
        refreshState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            observePlayerStateUseCase().collect { state ->
                _uiState.update { it.copy(playerState = state) }
            }
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            observeNetworkStatusUseCase().collect { status ->
                _uiState.update { it.copy(networkStatus = status) }
            }
        }
    }

    private fun observePlayerError() {
        viewModelScope.launch {
            observePlayerErrorUseCase().collect { error ->
                _uiState.update { it.copy(errorMessage = error) }
            }
        }
    }

    fun togglePlayStop() {
        viewModelScope.launch {
            if (_uiState.value.playerState == PlayerState.PLAYING) {
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

    fun refreshState() {
        viewModelScope.launch {
            refreshPlayerStateUseCase()
        }
    }

    private fun clearError() {
        viewModelScope.launch {
            clearErrorUseCase()
        }
    }
}