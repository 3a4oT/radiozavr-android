package com.rovenskyi.radiozavr.ui.main

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radiozavr.R
import com.rovenskyi.radiozavr.core.components.widgets.CircularCountdownIndicator
import com.rovenskyi.radiozavr.core.models.riddle.Riddle
import com.rovenskyi.radiozavr.core.models.riddle.RiddleAnswerMode
import com.rovenskyi.radiozavr.core.models.riddle.RiddleInterval
import com.rovenskyi.radiozavr.core.theme.LocalDimensions
import com.rovenskyi.radiozavr.core.theme.LocalIsTv
import com.rovenskyi.radiozavr.core.theme.LocalTvFocusColor
import kotlinx.coroutines.delay

private const val TV_INDICATOR_SIZE_DP = 76
private const val PHONE_INDICATOR_SIZE_DP = 60
private const val TV_STROKE_WIDTH_DP = 4
private const val PHONE_STROKE_WIDTH_DP = 3

private const val ANIMATION_DURATION_MS = 150
private const val FOCUS_SCALE = 1.02f

/**
 * Widget that displays rotating riddles.
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
    val uiState by viewModel.uiState.collectAsState()
    val isTv = LocalIsTv.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        uiState.riddle?.let { currentRiddle ->
            AnimatedContent(
                targetState = currentRiddle,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "riddle_rotation",
            ) { targetRiddle ->
                RiddleContent(
                    riddle = targetRiddle,
                    interval = uiState.interval,
                    riddleStartTime = uiState.riddleStartTime,
                    answerMode = uiState.answerMode,
                    onAnswerRevealed = { isAutomatic ->
                        viewModel.trackAnswerRevealed(isTv, isAutomatic)
                    },
                )
            }
        }
    }
}

@Composable
private fun RiddleContent(
    riddle: Riddle,
    interval: RiddleInterval,
    riddleStartTime: Long,
    answerMode: RiddleAnswerMode,
    onAnswerRevealed: (isAutomatic: Boolean) -> Unit,
) {
    val isTv = LocalIsTv.current
    val dimensions = LocalDimensions.current
    val focusColor = LocalTvFocusColor.current

    var showAnswer by remember(riddle.question) { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    AutoRevealEffect(answerMode, riddle, interval, riddleStartTime, showAnswer) {
        showAnswer = true
        onAnswerRevealed(true)
    }

    RiddleColumn(
        riddle = riddle,
        interval = interval,
        riddleStartTime = riddleStartTime,
        isTv = isTv,
        isFocused = isFocused,
        focusColor = focusColor,
        focusBorderWidth = dimensions.focusBorderWidth,
        showAnswer = showAnswer,
        onFocusChanged = { isFocused = it },
        onToggleAnswer = {
            if (!showAnswer) onAnswerRevealed(false)
            showAnswer = !showAnswer
        },
    )
}

@Composable
private fun AutoRevealEffect(
    answerMode: RiddleAnswerMode,
    riddle: Riddle,
    interval: RiddleInterval,
    riddleStartTime: Long,
    showAnswer: Boolean,
    onAutoReveal: () -> Unit,
) {
    LaunchedEffect(riddle, answerMode) {
        if (answerMode == RiddleAnswerMode.AUTOMATIC && !showAnswer) {
            val autoRevealDelayMs = (interval.seconds - interval.autoRevealSeconds) * 1000L
            val elapsed = System.currentTimeMillis() - riddleStartTime
            val remainingDelay = autoRevealDelayMs - elapsed

            if (remainingDelay > 0) {
                delay(remainingDelay)
            }
            onAutoReveal()
        }
    }
}

@Composable
private fun RiddleColumn(
    riddle: Riddle,
    interval: RiddleInterval,
    riddleStartTime: Long,
    isTv: Boolean,
    isFocused: Boolean,
    focusColor: Color,
    focusBorderWidth: Dp,
    showAnswer: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onToggleAnswer: () -> Unit,
) {
    val dimensions = LocalDimensions.current

    val scale by animateFloatAsState(
        targetValue = if (isFocused) FOCUS_SCALE else 1f,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "riddleScale",
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isFocused) focusBorderWidth else 0.dp,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "riddleFocusBorder",
    )

    val hintTextRes = if (isTv) R.string.riddle_press_ok_for_answer else R.string.riddle_tap_for_answer
    val accessibilityDescription = buildAccessibilityDescription(riddle, showAnswer, isTv)

    val borderModifier = if (isFocused) {
        Modifier.border(width = animatedBorderWidth, color = focusColor, shape = MaterialTheme.shapes.medium)
    } else {
        Modifier
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .then(borderModifier)
            .clickable(onClick = onToggleAnswer)
            .focusable()
            .padding(horizontal = dimensions.paddingLarge, vertical = dimensions.paddingMedium)
            .semantics {
                contentDescription = accessibilityDescription
                role = Role.Button
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularCountdownIndicator(
            durationSeconds = interval.seconds,
            animationKey = riddle,
            startTimeMillis = riddleStartTime,
            size = if (isTv) TV_INDICATOR_SIZE_DP.dp else PHONE_INDICATOR_SIZE_DP.dp,
            strokeWidth = if (isTv) TV_STROKE_WIDTH_DP.dp else PHONE_STROKE_WIDTH_DP.dp,
            warningSeconds = interval.warningSeconds,
        ) {
            Text(text = riddle.emoji, style = MaterialTheme.typography.headlineLarge)
        }
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        Text(text = riddle.question, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        RiddleAnswerHint(answer = riddle.answer, showAnswer = showAnswer, hintTextRes = hintTextRes)
    }
}

@Composable
private fun buildAccessibilityDescription(riddle: Riddle, showAnswer: Boolean, isTv: Boolean): String {
    val riddleDescriptionRes = if (isTv) R.string.riddle_content_description_tv else R.string.riddle_content_description
    val riddleDescription = stringResource(riddleDescriptionRes, riddle.question)
    return if (showAnswer) {
        val answerDescription = stringResource(R.string.riddle_answer_content_description, riddle.answer)
        "$riddleDescription $answerDescription"
    } else {
        riddleDescription
    }
}

@Composable
private fun RiddleAnswerHint(answer: String, showAnswer: Boolean, hintTextRes: Int) {
    if (showAnswer) {
        Text(
            text = "\uD83D\uDCA1 $answer",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
    } else {
        Text(
            text = stringResource(hintTextRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
