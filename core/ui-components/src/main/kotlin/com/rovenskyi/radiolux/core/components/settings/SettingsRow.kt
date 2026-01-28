package com.rovenskyi.radiolux.core.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rovenskyi.radiolux.core.theme.LocalDimensions

/**
 * Individual setting row with title, optional subtitle, and trailing content.
 * Supports click interaction and TV D-pad focus.
 *
 * @param title Main text for the setting
 * @param modifier Modifier to apply to the row
 * @param subtitle Optional secondary text
 * @param onClick Optional click handler
 * @param contentDescription Accessibility description
 * @param trailing Trailing content (toggle, value, icon, etc.)
 */
@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(onClick = onClick)
                        .focusable()
                } else {
                    Modifier
                },
            )
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
