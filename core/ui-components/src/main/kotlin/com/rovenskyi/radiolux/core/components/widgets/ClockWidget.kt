package com.rovenskyi.radiolux.core.components.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
private const val UPDATE_INTERVAL_MS = 1000L

/**
 * Widget that displays the current time.
 * Updates every second automatically.
 *
 * @param modifier Modifier for the text
 * @param style Text style (defaults to displayLarge)
 * @param formatter DateTimeFormatter for time format (defaults to HH:mm)
 * @param contentDescriptionProvider Lambda to generate accessibility description from formatted time
 */
@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayLarge,
    formatter: DateTimeFormatter = DEFAULT_TIME_FORMATTER,
    contentDescriptionProvider: (String) -> String = { it },
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(UPDATE_INTERVAL_MS)
            currentTime = LocalTime.now()
        }
    }

    val formattedTime = currentTime.format(formatter)
    val description = contentDescriptionProvider(formattedTime)

    Text(
        text = formattedTime,
        style = style,
        modifier = modifier.semantics {
            contentDescription = description
        },
    )
}
