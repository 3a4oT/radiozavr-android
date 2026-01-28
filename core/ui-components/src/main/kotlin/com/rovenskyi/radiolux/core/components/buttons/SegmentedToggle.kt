package com.rovenskyi.radiolux.core.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rovenskyi.radiolux.core.theme.LocalDimensions

/**
 * Segmented toggle for selecting between multiple options.
 * TV-friendly with D-pad navigation support.
 *
 * @param options List of option labels to display
 * @param selectedIndex Currently selected option index
 * @param onSelect Callback when an option is selected
 * @param modifier Modifier to apply to the toggle
 * @param contentDescriptionPrefix Prefix for accessibility descriptions
 */
@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentDescriptionPrefix: String = "",
) {
    val dimensions = LocalDimensions.current

    Row(modifier = modifier) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val shape = when (index) {
                0 -> RoundedCornerShape(
                    topStart = dimensions.cornerMedium,
                    bottomStart = dimensions.cornerMedium,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp,
                )
                options.lastIndex -> RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = dimensions.cornerMedium,
                    bottomEnd = dimensions.cornerMedium,
                )
                else -> RoundedCornerShape(0.dp)
            }

            OutlinedButton(
                onClick = { onSelect(index) },
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                ),
                modifier = Modifier.semantics {
                    this.contentDescription = "$contentDescriptionPrefix $label"
                },
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
