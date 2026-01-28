package com.rovenskyi.radiolux.core.components.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private const val BAR_COUNT = 4
private const val MIN_HEIGHT_FRACTION = 0.3f
private const val MAX_HEIGHT_FRACTION = 1.0f

/**
 * Audio visualizer with pulsing bars that animate when audio is playing.
 * Designed to be placed on either side of the play button.
 *
 * @param modifier Modifier for the container
 * @param barCount Number of bars to display
 * @param barWidth Width of each bar
 * @param maxBarHeight Maximum height of bars
 * @param barColor Color of the bars (defaults to primary color)
 * @param barSpacing Spacing between bars
 */
@Composable
fun AudioVisualizer(
    modifier: Modifier = Modifier,
    barCount: Int = BAR_COUNT,
    barWidth: Dp = 4.dp,
    maxBarHeight: Dp = 24.dp,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barSpacing: Dp = 3.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")

    // Create random phase offsets for each bar to make animation look organic
    val phaseOffsets = remember { List(barCount) { Random.nextFloat() * 1000f } }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(barCount) { index ->
            val animatedFraction by infiniteTransition.animateFloat(
                initialValue = MIN_HEIGHT_FRACTION,
                targetValue = MAX_HEIGHT_FRACTION,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (400 + phaseOffsets[index].toInt() % 200),
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "bar_$index",
            )

            // Apply phase offset by using different start delays
            val adjustedFraction = if (index % 2 == 0) animatedFraction else 1f - animatedFraction

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(maxBarHeight * adjustedFraction)
                    .clip(RoundedCornerShape(barWidth / 2))
                    .background(barColor),
            )
        }
    }
}
