package com.rovenskyi.radiolux.core.components.settings

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rovenskyi.radiolux.core.models.permission.PermissionStatus

/**
 * Displays an icon indicating the current status of a permission.
 *
 * - NOT_ASKED: Gray help icon
 * - GRANTED: Green check icon
 * - DENIED: Red cancel icon
 *
 * @param status The current permission status
 * @param contentDescription Accessibility description
 * @param modifier Modifier to apply to the icon
 */
@Composable
fun PermissionStatusIndicator(
    status: PermissionStatus,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val icon = when (status) {
        PermissionStatus.NOT_ASKED -> Icons.AutoMirrored.Filled.HelpOutline
        PermissionStatus.GRANTED -> Icons.Filled.CheckCircle
        PermissionStatus.DENIED -> Icons.Filled.Cancel
    }
    val tint = when (status) {
        PermissionStatus.NOT_ASKED -> MaterialTheme.colorScheme.outline
        PermissionStatus.GRANTED -> Color(0xFF4CAF50)
        PermissionStatus.DENIED -> MaterialTheme.colorScheme.error
    }

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(24.dp),
    )
}
