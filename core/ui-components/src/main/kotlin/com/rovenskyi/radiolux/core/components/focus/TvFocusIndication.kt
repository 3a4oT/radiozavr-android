package com.rovenskyi.radiolux.core.components.focus

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rovenskyi.radiolux.core.theme.LocalTvFocusColor

/**
 * Adds TV-friendly focus indication with animated border and scale effect.
 * Uses theme-aware focus color from [LocalTvFocusColor].
 *
 * When focused:
 * - Shows a visible border (Cyan on dark, Teal on light)
 * - Slightly scales up the element
 *
 * @param shape Shape for the focus border
 * @param borderWidth Width of the focus border
 * @param scaleOnFocus Scale factor when focused (1.0 = no scale)
 */
fun Modifier.tvFocusIndication(
    shape: Shape,
    borderWidth: Dp = 3.dp,
    scaleOnFocus: Float = 1.05f,
): Modifier = composed {
    val focusColor = LocalTvFocusColor.current
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleOnFocus else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "focusScale",
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused) borderWidth else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "focusBorder",
    )

    this
        .onFocusChanged { isFocused = it.isFocused }
        .scale(scale)
        .then(
            if (isFocused) {
                Modifier.border(
                    width = animatedBorderWidth,
                    color = focusColor,
                    shape = shape,
                )
            } else {
                Modifier
            }
        )
        .focusable()
}

/**
 * Simplified version that only tracks focus state.
 * Use when you need custom focus handling.
 *
 * @param onFocusChanged Callback when focus state changes
 */
fun Modifier.onTvFocus(
    onFocusChanged: (Boolean) -> Unit,
): Modifier = composed {
    this
        .onFocusChanged { onFocusChanged(it.isFocused) }
        .focusable()
}
