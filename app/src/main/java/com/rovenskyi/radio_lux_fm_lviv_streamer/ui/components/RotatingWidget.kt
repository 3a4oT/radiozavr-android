package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RotatingWidgetViewModel
import com.rovenskyi.radiolux.core.models.riddle.Riddle
import com.rovenskyi.radiolux.core.theme.LocalIsTv
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val ANSWER_REVEAL_TIME: LocalTime = LocalTime.of(22, 30)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Widget that displays clock and rotating riddles.
 * Clock is always visible on TV, riddles rotate every 20 seconds.
 */
@Composable
fun RotatingWidget(
    modifier: Modifier = Modifier,
    viewModel: RotatingWidgetViewModel = hiltViewModel(),
) {
    val isTv = LocalIsTv.current
    val riddle by viewModel.currentRiddle.collectAsState()
    val riddleIndex by viewModel.riddleIndex.collectAsState()
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000L)
            currentTime = LocalTime.now()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Clock - only on TV
        if (isTv) {
            ClockContent(currentTime)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Riddle - rotating with animation
        riddle?.let { currentRiddle ->
            AnimatedContent(
                targetState = riddleIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "riddle_rotation",
            ) { _ ->
                RiddleContent(
                    riddle = currentRiddle,
                    isAfterRevealTime = currentTime >= ANSWER_REVEAL_TIME,
                )
            }
        }
    }
}

@Composable
private fun ClockContent(currentTime: LocalTime) {
    Text(
        text = currentTime.format(timeFormatter),
        style = MaterialTheme.typography.displayLarge,
    )
}

@Composable
private fun RiddleContent(
    riddle: Riddle,
    isAfterRevealTime: Boolean,
) {
    var showAnswer by rememberSaveable(riddle.question) { mutableStateOf(false) }
    val shouldShowAnswer = showAnswer || isAfterRevealTime

    Column(
        modifier = Modifier
            .clickable { showAnswer = !showAnswer }
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = riddle.emoji,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = riddle.question,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (shouldShowAnswer) {
            Text(
                text = "\uD83D\uDCA1 ${riddle.answer}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Text(
                text = "Натисни для відповіді",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
