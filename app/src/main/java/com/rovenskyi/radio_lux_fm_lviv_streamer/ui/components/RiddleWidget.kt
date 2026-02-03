package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RiddleWidgetViewModel
import com.rovenskyi.radiolux.core.models.riddle.Riddle
import com.rovenskyi.radiolux.core.theme.LocalDimensions
import com.rovenskyi.radiolux.core.theme.LocalIsTv
import com.rovenskyi.radiolux.core.theme.LocalTvFocusColor
import java.time.LocalTime

private val ANSWER_REVEAL_TIME: LocalTime = LocalTime.of(22, 30)

private const val ANIMATION_DURATION_MS = 150
private const val FOCUS_SCALE = 1.02f

/**
 * Widget that displays rotating riddles.
 * Riddles rotate every 20 seconds.
 *
 * Accessibility:
 * - Full TalkBack support with content descriptions
 * - D-pad navigation support for TV
 * - Visual focus indicator for TV navigation
 */
@Composable
fun RiddleWidget(
    modifier: Modifier = Modifier,
    viewModel: RiddleWidgetViewModel = hiltViewModel(),
) {
    val riddle by viewModel.currentRiddle.collectAsState()
    val riddleIndex by viewModel.riddleIndex.collectAsState()

    riddle?.let { currentRiddle ->
        AnimatedContent(
            targetState = riddleIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "riddle_rotation",
            modifier = modifier,
        ) { _ ->
            RiddleContent(riddle = currentRiddle)
        }
    }
}

@Composable
private fun RiddleContent(
    riddle: Riddle,
) {
    val isTv = LocalIsTv.current
    val dimensions = LocalDimensions.current
    val focusColor = LocalTvFocusColor.current
    val currentTime = LocalTime.now()
    val isAfterRevealTime = currentTime >= ANSWER_REVEAL_TIME

    var showAnswer by rememberSaveable(riddle.question) { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val shouldShowAnswer = showAnswer || isAfterRevealTime

    // Focus animations (only meaningful on TV)
    val scale by animateFloatAsState(
        targetValue = if (isFocused) FOCUS_SCALE else 1f,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "riddleScale",
    )

    val focusBorderWidth by animateDpAsState(
        targetValue = if (isFocused) dimensions.focusBorderWidth else 0.dp,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "riddleFocusBorder",
    )

    // Platform-specific strings
    val riddleDescriptionRes = if (isTv) {
        R.string.riddle_content_description_tv
    } else {
        R.string.riddle_content_description
    }
    val hintTextRes = if (isTv) {
        R.string.riddle_press_ok_for_answer
    } else {
        R.string.riddle_tap_for_answer
    }

    // Accessibility descriptions
    val riddleDescription = stringResource(riddleDescriptionRes, riddle.question)
    val answerDescription = stringResource(R.string.riddle_answer_content_description, riddle.answer)
    val accessibilityDescription = if (shouldShowAnswer) {
        "$riddleDescription $answerDescription"
    } else {
        riddleDescription
    }

    Column(
        modifier = Modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = focusBorderWidth,
                        color = focusColor,
                        shape = MaterialTheme.shapes.medium,
                    )
                } else {
                    Modifier
                },
            )
            .clickable { showAnswer = !showAnswer }
            .focusable()
            .padding(dimensions.paddingMedium)
            .semantics {
                contentDescription = accessibilityDescription
                role = Role.Button
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = riddle.emoji,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        Text(
            text = riddle.question,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
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
                text = stringResource(hintTextRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
