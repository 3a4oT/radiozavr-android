package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

val predefinedColors = listOf(
    Color(0xFFFDFCFB),
    Color(0xFFE2D1C3),

    Color(0xFF764BA2),
    Color(0xFF667EEA),

    Color(0xFFA1C4FD),
    Color(0xFFC2E9FB),

    Color(0xFFFFECD2),
    Color(0xFFFCB69F),

    Color(0xFF09203F),
    Color(0xFF537895),

    Color(0xFF93A5CF),

    Color(0xFF614385),
    Color(0xFF516395)
)

@Composable
fun RelaxingBackground(modifier: Modifier = Modifier) {
    var color1 by remember { mutableStateOf(predefinedColors.random()) }
    var color2 by remember { mutableStateOf(predefinedColors.random()) }

    val animatedColor1 by animateColorAsState(targetValue = color1, animationSpec = tween(durationMillis = 3000))
    val animatedColor2 by animateColorAsState(targetValue = color2, animationSpec = tween(durationMillis = 3000))

    LaunchedEffect(Unit) {
        while (true) {
            val random1 = predefinedColors.random()
            val random2 = predefinedColors.random()
            if (random1 == random2) {
                color1 = predefinedColors.random()
                color2 = predefinedColors.random()
            } else {
                color1 = random1
                color2 = random2
            }
            delay(15000L) // Change colors every 15 seconds
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(animatedColor1, animatedColor2)
                )
            )
    )
}