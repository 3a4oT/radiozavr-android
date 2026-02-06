package com.rovenskyi.radiozavr.core.components.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rovenskyi.radiozavr.core.theme.LocalDimensions
import com.rovenskyi.radiozavr.core.theme.LocalTvFocusColor
import kotlinx.coroutines.launch

/**
 * Individual setting row with title, optional subtitle, and trailing content.
 * Supports click interaction and TV D-pad focus with visual indication.
 * Automatically scrolls into view when focused (essential for TV D-pad navigation).
 *
 * @param title Main text for the setting
 * @param modifier Modifier to apply to the row
 * @param subtitle Optional secondary text
 * @param onClick Optional click handler
 * @param contentDescription Accessibility description
 * @param focusRequester Optional FocusRequester for focus restoration (used with focusRestorer)
 * @param trailing Trailing content (toggle, value, icon, etc.)
 */
@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
    focusRequester: FocusRequester? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val dimensions = LocalDimensions.current
    val focusColor = LocalTvFocusColor.current
    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "settingsRowScale",
    )

    val focusBorderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "settingsRowBorder",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                } else {
                    Modifier
                },
            )
            .bringIntoViewRequester(bringIntoViewRequester)
            .scale(scale)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = focusBorderWidth,
                        color = focusColor,
                        shape = MaterialTheme.shapes.small,
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .focusable()
            .padding(dimensions.paddingMedium)
            .semantics {
                contentDescription?.let {
                    this.contentDescription = it
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}
