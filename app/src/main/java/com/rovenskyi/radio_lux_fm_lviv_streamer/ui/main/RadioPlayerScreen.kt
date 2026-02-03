package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main

import android.app.Activity
import android.view.WindowManager
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.PlayerState
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.RiddleWidget
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.NetworkErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel
import com.rovenskyi.radiolux.core.components.background.RelaxingBackground
import com.rovenskyi.radiolux.core.components.player.PlayerBar
import com.rovenskyi.radiolux.core.components.player.PlayerBarState
import com.rovenskyi.radiolux.core.components.widgets.ClockWidget
import com.rovenskyi.radiolux.core.theme.LocalDimensions
import com.rovenskyi.radiolux.core.theme.LocalIsTv
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
    val isTv = LocalIsTv.current

    // Auto-play on app start if enabled (runs only once per app session via ViewModel flag)
    LaunchedEffect(Unit) {
        delay(AUTO_PLAY_STATE_SYNC_DELAY_MS)
        viewModel.tryAutoPlay(autoPlayEnabled)
    }

    // Request audio permission 3 seconds after successful playback starts
    LaunchedEffect(uiState.playerState) {
        if (uiState.playerState == PlayerState.PLAYING) {
            delay(AUDIO_PERMISSION_DELAY_MS)
            onRequestAudioPermission()
        }
    }

    // Keep screen on during playback (prevents TV screensaver)
    KeepScreenOn(enabled = isTv && uiState.playerState == PlayerState.PLAYING)

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
        // Top: Widget area (clock on TV, riddles everywhere)
        WidgetArea(modifier = Modifier.weight(1f))

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

@Composable
private fun WidgetArea(modifier: Modifier = Modifier) {
    val isTv = LocalIsTv.current
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (isTv) dimensions.safeAreaVertical else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Clock - only on TV (smaller font, fixed at top with safe area)
        if (isTv) {
            ClockWidget(style = MaterialTheme.typography.displaySmall)
        }

        // Riddles - takes all remaining space between clock and PlayerBar
        // RiddleWidget handles centering internally - no spacer needed
        RiddleWidget(
            modifier = Modifier.weight(1f),
        )
    }
}

private fun PlayerState.toPlayerBarState(): PlayerBarState = when (this) {
    PlayerState.STOPPED -> PlayerBarState.STOPPED
    PlayerState.LOADING -> PlayerBarState.BUFFERING
    PlayerState.PLAYING -> PlayerBarState.PLAYING
    PlayerState.ERROR -> PlayerBarState.ERROR
}

/**
 * Manages FLAG_KEEP_SCREEN_ON window flag.
 * Prevents screensaver on TV during playback.
 */
@Composable
private fun KeepScreenOn(enabled: Boolean) {
    val context = LocalContext.current
    DisposableEffect(enabled) {
        val window = (context as? Activity)?.window
        if (enabled) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
