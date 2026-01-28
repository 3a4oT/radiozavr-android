package com.rovenskyi.radiolux.core.components.player

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rovenskyi.radiolux.core.theme.LocalTvFocusColor

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
 * @param visualizerAmplitudes Optional list of amplitude values (0-1) from real audio capture.
 *                              If null, uses simulated random animation.
 * @param errorTitle Friendly localized error message shown when state is ERROR
 * @param errorDetails Technical error details shown in parentheses when state is ERROR
 * @param requestInitialFocus If true, the play button requests focus on first composition (TV)
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
    visualizerAmplitudes: List<Float>? = null,
    errorTitle: String? = null,
    errorDetails: String? = null,
    requestInitialFocus: Boolean = true,
) {
    val focusRequester = remember { FocusRequester() }

    // Request focus on first composition and after state changes
    // This ensures focus returns to play button after buffering
    LaunchedEffect(state, requestInitialFocus) {
        if (requestInitialFocus) {
            focusRequester.requestFocus()
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Title: show error message when ERROR state, otherwise branding
        if (state == PlayerBarState.ERROR && errorTitle != null) {
            val displayText = if (errorDetails != null) {
                "$errorTitle ($errorDetails)"
            } else {
                errorTitle
            }
            Text(
                text = displayText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
            )
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

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
                    amplitudes = visualizerAmplitudes,
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Center: Button with optional loading overlay
            // Button is always present to maintain focus during state transitions
            Box(contentAlignment = Alignment.Center) {
                when (state) {
                    PlayerBarState.ERROR -> {
                        PlayerButton(
                            icon = retryIcon,
                            contentDescription = retryContentDescription,
                            onClick = onRetryClick,
                            size = buttonSize,
                            containerColor = MaterialTheme.colorScheme.error,
                            focusRequester = focusRequester,
                        )
                    }

                    PlayerBarState.BUFFERING -> {
                        // Show faded button underneath to keep focus
                        PlayerButton(
                            icon = playIcon,
                            contentDescription = bufferingContentDescription,
                            onClick = { }, // No-op during buffering
                            size = buttonSize,
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            focusRequester = focusRequester,
                        )
                        // Progress indicator overlay
                        CircularProgressIndicator(
                            modifier = Modifier.size(buttonSize),
                            strokeWidth = 4.dp,
                        )
                    }

                    else -> {
                        val isPlaying = state == PlayerBarState.PLAYING
                        PlayerButton(
                            icon = if (isPlaying) pauseIcon else playIcon,
                            contentDescription = if (isPlaying) pauseContentDescription else playContentDescription,
                            onClick = onPlayClick,
                            size = buttonSize,
                            focusRequester = focusRequester,
                        )
                    }
                }
            }

            // Right visualizer (only when playing)
            if (state == PlayerBarState.PLAYING) {
                Spacer(modifier = Modifier.width(16.dp))
                AudioVisualizer(
                    barCount = visualizerBarCount,
                    barColor = MaterialTheme.colorScheme.primary,
                    amplitudes = visualizerAmplitudes,
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
    focusRequester: FocusRequester? = null,
) {
    val focusColor = LocalTvFocusColor.current
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.15f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "playerButtonScale",
    )

    val focusBorderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "playerButtonBorder",
    )

    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .scale(scale)
            .then(
                if (focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                } else {
                    Modifier
                }
            )
            .onFocusChanged { isFocused = it.isFocused }
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = focusBorderWidth,
                        color = focusColor,
                        shape = CircleShape,
                    )
                } else {
                    Modifier
                }
            ),
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
