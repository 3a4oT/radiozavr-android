package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000L)
        }
    }

    Text(text = currentTime, style = MaterialTheme.typography.headlineLarge, modifier = modifier)
}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(Date())
}