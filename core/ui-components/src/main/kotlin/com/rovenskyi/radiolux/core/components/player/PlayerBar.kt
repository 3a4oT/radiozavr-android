package com.rovenskyi.radiolux.core.components.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * State of the player for UI representation.
 */
enum class PlayerBarState {
    STOPPED,
    BUFFERING,
    PLAYING,
    ERROR,
}

/**
 * Modern player bar with branding, play button, and audio visualizer.
 * The visualizer bars surround the play button when playing.
 *
 * @param state Current player state
 * @param title Branding title (e.g., "Lux FM 104.7")
 * @param playIcon Icon for play action
 * @param pauseIcon Icon for pause action
 * @param retryIcon Icon for retry action (error state)
 * @param playContentDescription Accessibility description for play button
 * @param pauseContentDescription Accessibility description for pause button
 * @param retryContentDescription Accessibility description for retry button
 * @param bufferingContentDescription Accessibility description for buffering state
 * @param onPlayClick Called when play/pause button is clicked
 * @param onRetryClick Called when retry button is clicked (error state)
 * @param modifier Modifier for the container
 * @param buttonSize Size of the play button
 * @param visualizerBarCount Number of bars on each side of the button
 */
@Composable
fun PlayerBar(
    state: PlayerBarState,
    title: String,
    playIcon: ImageVector,
    pauseIcon: ImageVector,
    retryIcon: ImageVector,
    playContentDescription: String,
    pauseContentDescription: String,
    retryContentDescription: String,
    bufferingContentDescription: String,
    onPlayClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 64.dp,
    visualizerBarCount: Int = 4,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Branding title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Player controls with visualizer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            // Left visualizer (only when playing)
            if (state == PlayerBarState.PLAYING) {
                AudioVisualizer(
                    barCount = visualizerBarCount,
                    barColor = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Center: Button or Loading indicator
            when (state) {
                PlayerBarState.BUFFERING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(buttonSize)
                            .semantics {
                                contentDescription = bufferingContentDescription
                            },
                        strokeWidth = 4.dp,
                    )
                }

                PlayerBarState.ERROR -> {
                    PlayerButton(
                        icon = retryIcon,
                        contentDescription = retryContentDescription,
                        onClick = onRetryClick,
                        size = buttonSize,
                        containerColor = MaterialTheme.colorScheme.error,
                    )
                }

                else -> {
                    val isPlaying = state == PlayerBarState.PLAYING
                    PlayerButton(
                        icon = if (isPlaying) pauseIcon else playIcon,
                        contentDescription = if (isPlaying) pauseContentDescription else playContentDescription,
                        onClick = onPlayClick,
                        size = buttonSize,
                    )
                }
            }

            // Right visualizer (only when playing)
            if (state == PlayerBarState.PLAYING) {
                Spacer(modifier = Modifier.width(16.dp))
                AudioVisualizer(
                    barCount = visualizerBarCount,
                    barColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun PlayerButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(size),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}
