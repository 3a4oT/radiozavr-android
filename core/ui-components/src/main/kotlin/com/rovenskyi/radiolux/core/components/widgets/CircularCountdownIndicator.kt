package com.rovenskyi.radiolux.core.components.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val START_ANGLE = -90f

/**
 * Circular countdown indicator that animates from full to empty.
 * Changes color in the last [warningSeconds] seconds.
 *
 * @param durationSeconds Total countdown duration in seconds
 * @param animationKey Key to restart animation (e.g., item ID)
 * @param modifier Modifier for the container
 * @param size Size of the indicator
 * @param strokeWidth Width of the progress arc
 * @param normalColor Color during normal countdown
 * @param warningColor Color during warning phase (last N seconds)
 * @param warningSeconds Seconds before end when color starts transitioning
 * @param content Content to display in the center (e.g., icon, emoji)
 */
@Composable
fun CircularCountdownIndicator(
    durationSeconds: Int,
    animationKey: Any,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 3.dp,
    normalColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    warningColor: Color = MaterialTheme.colorScheme.primary,
    warningSeconds: Int = 3,
    content: @Composable () -> Unit = {},
) {
    val warningThreshold = warningSeconds.toFloat() / durationSeconds

    // Compose Animatable - automatically invalidates on each frame
    val progress = remember { Animatable(1f) }

    // Restart animation when key changes
    LaunchedEffect(animationKey, durationSeconds) {
        progress.snapTo(1f)
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = durationSeconds * 1000,
                easing = LinearEasing,
            ),
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Progress arc
        val currentProgress = progress.value
        val arcColor = if (currentProgress <= warningThreshold) {
            val colorProgress = 1f - (currentProgress / warningThreshold)
            lerp(normalColor, warningColor, colorProgress.coerceIn(0f, 1f))
        } else {
            normalColor
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            val sweepAngle = currentProgress * 360f
            drawArc(
                color = arcColor,
                startAngle = START_ANGLE,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
            )
        }

        // Center content
        content()
    }
}
