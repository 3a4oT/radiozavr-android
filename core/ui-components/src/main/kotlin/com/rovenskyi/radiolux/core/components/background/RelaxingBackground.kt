package com.rovenskyi.radiolux.core.components.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Simple themed background using Material3 surface color.
 *
 * @param modifier Modifier to apply to the background
 * @param content Content to display on top of the background
 */
@Composable
fun RelaxingBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        content()
    }
}
