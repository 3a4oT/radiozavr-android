package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.PlayerState
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.ClockWidget
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.LoadingIndicator
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.RelaxingBackground
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.NetworkErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.PlayerErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel

@Composable
fun RadioPlayerScreen(viewModel: RadioPlayerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        RelaxingBackground()

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
                    onTogglePlayStop = { viewModel.togglePlayStop() }
                )
            }
        }
    }
}

@Composable
fun RadioPlayerContent(
    playerState: PlayerState,
    onTogglePlayStop: () -> Unit
) {
    if (playerState == PlayerState.LOADING) {
        LoadingIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClockWidget()
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp),
                painter = painterResource(id = R.drawable.logo_lux),
                contentDescription = null
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
                    .focusRequester(focusRequester)
            ) {
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    painter = painterResource(id = if (playerState == PlayerState.PLAYING) R.drawable.ic_stop else R.drawable.ic_play),
                    contentDescription = null
                )
            }
        }
    }
}