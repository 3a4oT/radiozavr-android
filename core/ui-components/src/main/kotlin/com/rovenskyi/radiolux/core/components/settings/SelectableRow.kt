package com.rovenskyi.radiolux.core.components.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rovenskyi.radiolux.core.theme.LocalDimensions
import com.rovenskyi.radiolux.core.theme.LocalTvFocusColor

/**
 * Selectable row with radio button for single-choice lists.
 * TV-friendly with D-pad navigation support.
 *
 * @param text Label text to display
 * @param selected Whether this item is currently selected
 * @param onClick Called when the row is clicked
 * @param modifier Modifier to apply to the row
 * @param contentDescription Accessibility description for the row
 */
@Composable
fun SelectableRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = text,
) {
    val dimensions = LocalDimensions.current
    val focusColor = LocalTvFocusColor.current
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "selectableRowScale",
    )

    val focusBorderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "selectableRowBorder",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
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
            .clickable(onClick = onClick)
            .padding(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.paddingSmall,
            )
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.RadioButton
            },
    ) {
        RadioButton(
            selected = selected,
            onClick = null, // Handled by row click
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = dimensions.paddingMedium),
        )
    }
}
