package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main

import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.RelaxingBackground
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.ClockWidget
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components.LoadingIndicator
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.NetworkErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error.PlayerErrorScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel

@Composable
fun RadioPlayerScreen(viewModel: RadioPlayerViewModel = viewModel()) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val networkError by viewModel.playerError.collectAsState()
    val errorMessage by viewModel.playerErrorMessage.collectAsState()

    val isLoading = viewModel.playerIsLoadingLiveData.observeAsState(initial = false)
    val playerErrorMessage = viewModel.playerErrorLiveData.observeAsState()
    val networkStatus by viewModel.networkStatusLiveData.observeAsState(initial = true)

    playerErrorMessage.value?.let {
        viewModel.handlePlayerError(it)
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        RelaxingBackground()
        if (networkError) {
            PlayerErrorScreen(errorMessage) { viewModel.retry() }
        } else if (!networkStatus) {
            NetworkErrorScreen()
        } else {
            if (isLoading.value) {
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
                    Button(
                        onClick = {
                            viewModel.togglePlayStop()
                        },
                        modifier = Modifier
                            .width(if (screenWidth > 600.dp) 400.dp else 150.dp)
                            .height(if (screenHeight > 600.dp) 80.dp else 60.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp),
                            painter = painterResource(id = if (isPlaying) R.drawable.ic_stop else R.drawable.ic_play),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}