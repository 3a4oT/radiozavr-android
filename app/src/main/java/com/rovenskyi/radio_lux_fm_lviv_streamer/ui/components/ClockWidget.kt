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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Widget that displays the current time.
 * Updates every second.
 *
 * Accessibility:
 * - Full TalkBack support with content description
 */
@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000L)
            currentTime = LocalTime.now()
        }
    }

    val formattedTime = currentTime.format(timeFormatter)
    val clockDescription = stringResource(R.string.clock_content_description, formattedTime)

    Text(
        text = formattedTime,
        style = MaterialTheme.typography.displayLarge,
        modifier = modifier.semantics {
            contentDescription = clockDescription
        },
    )
}
