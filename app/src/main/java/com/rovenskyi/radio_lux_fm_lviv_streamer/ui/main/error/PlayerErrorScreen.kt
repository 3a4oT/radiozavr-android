package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.error

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rovenskyi.radio_lux_fm_lviv_streamer.R

@Composable
fun PlayerErrorScreen(errorMessage: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.player_error), fontSize = 20.sp)
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry_button))
        }
    }
}
