package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.PlayerState
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.ClockWidget
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.NetworkErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel
import com.rovenskyi.radiolux.core.components.background.RelaxingBackground
import com.rovenskyi.radiolux.core.components.player.PlayerBar
import com.rovenskyi.radiolux.core.components.player.PlayerBarState
import com.rovenskyi.radiolux.core.theme.LocalDimensions
import kotlinx.coroutines.delay

private const val AUDIO_PERMISSION_DELAY_MS = 3000L
private const val AUTO_PLAY_STATE_SYNC_DELAY_MS = 300L

@Composable
fun RadioPlayerScreen(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRequestAudioPermission: () -> Unit = {},
    viewModel: RadioPlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val visualizerAmplitudes by viewModel.visualizerAmplitudes.collectAsState()
    val autoPlayEnabled by viewModel.autoPlayEnabled.collectAsState()
    val dimensions = LocalDimensions.current

    // Auto-play on app start if enabled (run once with delay for state sync)
    LaunchedEffect(Unit) {
        delay(AUTO_PLAY_STATE_SYNC_DELAY_MS)
        if (autoPlayEnabled && uiState.playerState == PlayerState.STOPPED) {
            viewModel.play()
        }
    }

    // Request audio permission 3 seconds after successful playback starts
    LaunchedEffect(uiState.playerState) {
        if (uiState.playerState == PlayerState.PLAYING) {
            delay(AUDIO_PERMISSION_DELAY_MS)
            onRequestAudioPermission()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        RelaxingBackground()

        // Settings button in top-right corner
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimensions.paddingMedium),
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_title),
            )
        }

        when {
            uiState.networkStatus == NetworkStatus.UNAVAILABLE -> {
                NetworkErrorScreen { viewModel.retry() }
            }
            else -> {
                RadioPlayerContent(
                    playerState = uiState.playerState,
                    errorMessage = uiState.errorMessage,
                    visualizerAmplitudes = visualizerAmplitudes,
                    onTogglePlayStop = { viewModel.togglePlayStop() },
                    onRetry = { viewModel.retry() },
                )
            }
        }
    }
}

@Composable
private fun RadioPlayerContent(
    playerState: PlayerState,
    errorMessage: String?,
    visualizerAmplitudes: List<Float>?,
    onTogglePlayStop: () -> Unit,
    onRetry: () -> Unit,
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.paddingMedium),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top: Widget area (will be WidgetStack in future)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            // For now, show clock. Will be replaced with WidgetStack
            ClockWidget()
        }

        // Bottom: Player bar with branding and visualizer
        PlayerBar(
            state = playerState.toPlayerBarState(),
            title = stringResource(R.string.notitification_content_title),
            playIcon = Icons.Default.PlayArrow,
            pauseIcon = Icons.Default.Stop,
            retryIcon = Icons.Default.Refresh,
            playContentDescription = stringResource(R.string.play_button),
            pauseContentDescription = stringResource(R.string.player_pause),
            retryContentDescription = stringResource(R.string.retry_button),
            bufferingContentDescription = stringResource(R.string.player_buffering),
            onPlayClick = onTogglePlayStop,
            onRetryClick = onRetry,
            visualizerAmplitudes = visualizerAmplitudes,
            errorTitle = stringResource(R.string.player_error_friendly),
            errorDetails = errorMessage,
            modifier = Modifier.padding(bottom = dimensions.paddingLarge),
        )
    }
}

private fun PlayerState.toPlayerBarState(): PlayerBarState = when (this) {
    PlayerState.STOPPED -> PlayerBarState.STOPPED
    PlayerState.LOADING -> PlayerBarState.BUFFERING
    PlayerState.PLAYING -> PlayerBarState.PLAYING
    PlayerState.ERROR -> PlayerBarState.ERROR
}
