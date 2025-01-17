package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rovenskyi.radio_lux_fm_lviv_streamer.R

@Composable
fun NetworkErrorScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.network_error), fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
    }
}