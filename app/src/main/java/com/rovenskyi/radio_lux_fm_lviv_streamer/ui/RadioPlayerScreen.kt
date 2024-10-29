package com.rovenskyi.radio_lux_fm_lviv_streamer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel

@Composable
fun RadioPlayerScreen(viewModel: RadioPlayerViewModel) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val networkError by viewModel.networkError.collectAsState()

    if (networkError) {
        NetworkErrorScreen { viewModel.retry() }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    viewModel.togglePlayStop()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(if (isPlaying) "Stop" else "Play", fontSize = 20.sp)
            }
        }
    }
}
