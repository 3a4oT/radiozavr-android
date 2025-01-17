package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import kotlinx.coroutines.delay

@Composable
fun LoadingIndicator() {
    val angle = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            angle.value += 10f
            delay(16)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_lux),
            contentDescription = "процес завантаження",
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer(rotationZ = angle.value % 360)
        )
    }
}
