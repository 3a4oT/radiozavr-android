package com.rovenskyi.radiolux.core.components.background

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.rovenskyi.radiolux.core.theme.RadioLuxTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val COLOR_CHANGE_INTERVAL_MS = 60_000L
private const val ANIMATION_DURATION_MS = 3_000

/**
 * Animated gradient background that cycles through curated colors.
 * Colors adapt to current theme (dark/light mode).
 *
 * @param modifier Modifier to apply to the background
 * @param content Optional content to display on top of the background
 */
@Composable
fun RelaxingBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val palette = RadioLuxTheme.gradientPalette

    var colorIndex1 by remember { mutableIntStateOf(0) }
    var colorIndex2 by remember { mutableIntStateOf(1) }

    val color1 = remember(palette, colorIndex1) { palette[colorIndex1 % palette.size] }
    val color2 = remember(palette, colorIndex2) { palette[colorIndex2 % palette.size] }

    val animatedColor1 by animateColorAsState(
        targetValue = color1,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "gradientColor1",
    )
    val animatedColor2 by animateColorAsState(
        targetValue = color2,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "gradientColor2",
    )

    LaunchedEffect(palette) {
        while (isActive) {
            delay(COLOR_CHANGE_INTERVAL_MS)
            colorIndex1 = (colorIndex1 + 2) % palette.size
            colorIndex2 = (colorIndex2 + 2) % palette.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(animatedColor1, animatedColor2),
                ),
            ),
    ) {
        content()
    }
}
