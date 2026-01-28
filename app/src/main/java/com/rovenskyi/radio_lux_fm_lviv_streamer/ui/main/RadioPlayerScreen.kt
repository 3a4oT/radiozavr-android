package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radiolux.core.components.background.RelaxingBackground
import com.rovenskyi.radiolux.core.theme.LocalDimensions
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.PlayerState
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.ClockWidget
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.LoadingIndicator
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.NetworkErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.PlayerErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel

@Composable
fun RadioPlayerScreen(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RadioPlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val dimensions = LocalDimensions.current

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
            uiState.playerState == PlayerState.ERROR -> {
                PlayerErrorScreen(uiState.errorMessage) { viewModel.retry() }
            }
            uiState.networkStatus == NetworkStatus.UNAVAILABLE -> {
                NetworkErrorScreen { viewModel.retry() }
            }
            else -> {
                RadioPlayerContent(
                    playerState = uiState.playerState,
                    onTogglePlayStop = { viewModel.togglePlayStop() },
                )
            }
        }
    }
}

@Composable
fun RadioPlayerContent(
    playerState: PlayerState,
    onTogglePlayStop: () -> Unit,
) {
    if (playerState == PlayerState.LOADING) {
        LoadingIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ClockWidget()
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp),
                painter = painterResource(id = R.drawable.logo_lux),
                contentDescription = stringResource(R.string.app_name),
            )
            Spacer(modifier = Modifier.height(20.dp))
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            Button(
                onClick = onTogglePlayStop,
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .focusRequester(focusRequester),
            ) {
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    painter = painterResource(
                        id = if (playerState == PlayerState.PLAYING) {
                            R.drawable.ic_stop
                        } else {
                            R.drawable.ic_play
                        },
                    ),
                    contentDescription = if (playerState == PlayerState.PLAYING) {
                        stringResource(R.string.stop_button)
                    } else {
                        stringResource(R.string.play_button)
                    },
                )
            }
        }
    }
}
