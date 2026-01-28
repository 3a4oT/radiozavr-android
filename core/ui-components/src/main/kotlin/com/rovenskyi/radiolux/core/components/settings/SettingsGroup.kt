package com.rovenskyi.radiolux.core.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rovenskyi.radiolux.core.theme.LocalDimensions

/**
 * Container for a group of related settings.
 * Provides a title and visual grouping with surface background.
 *
 * @param title Group title displayed above the settings
 * @param modifier Modifier to apply to the container
 * @param content Settings rows to display in the group
 */
@Composable
fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spacingSmall),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.paddingSmall,
            ),
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingMedium),
        ) {
            Column {
                content()
            }
        }
    }
}
