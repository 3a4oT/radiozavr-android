package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            currentTime = getCurrentTime()
            delay(1000L)
        }
    }

    Text(text = currentTime, style = MaterialTheme.typography.headlineLarge, modifier = modifier)
}

private fun getCurrentTime(): String {
    return LocalTime.now().format(timeFormatter)
}
