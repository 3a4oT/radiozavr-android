package com.rovenskyi.radiozavr.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.rovenskyi.radiolux.core.theme.LocalIsTv
import com.rovenskyi.radiolux.core.theme.TvCompactTypography

/**
 * Provides compact typography for Settings screens on TV.
 *
 * On TV, full-size typography (TvTypography) is designed for 10-foot viewing
 * and can be too large for dense content like settings lists.
 * This wrapper uses TvCompactTypography (~1.2x scale instead of ~1.5x)
 * for better readability and more content on screen.
 *
 * On phone, uses standard MaterialTheme typography unchanged.
 */
@Composable
fun SettingsThemeProvider(content: @Composable () -> Unit) {
    val isTv = LocalIsTv.current
    val typography = if (isTv) TvCompactTypography else MaterialTheme.typography

    MaterialTheme(typography = typography, content = content)
}
