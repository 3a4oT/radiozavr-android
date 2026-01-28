package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radiolux.core.components.buttons.SegmentedToggle
import com.rovenskyi.radiolux.core.components.settings.SettingsGroup
import com.rovenskyi.radiolux.core.components.settings.SettingsRow
import com.rovenskyi.radiolux.core.models.theme.ThemeMode
import com.rovenskyi.radiolux.core.theme.LocalDimensions
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val dimensions = LocalDimensions.current
    val themeMode by themeViewModel.themeMode.collectAsState()

    val themeModeOptions = listOf(
        stringResource(R.string.settings_theme_light),
        stringResource(R.string.settings_theme_dark),
        stringResource(R.string.settings_theme_auto),
    )

    val selectedThemeIndex = when (themeMode) {
        ThemeMode.LIGHT -> 0
        ThemeMode.DARK -> 1
        ThemeMode.AUTO -> 2
    }

    val currentThemeLabel = themeModeOptions[selectedThemeIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Appearance settings group
            SettingsGroup(
                title = stringResource(R.string.settings_group_appearance),
            ) {
                SettingsRow(
                    title = stringResource(R.string.settings_theme_title),
                    subtitle = stringResource(R.string.settings_theme_subtitle),
                    contentDescription = stringResource(
                        R.string.settings_theme_content_description,
                        currentThemeLabel,
                    ),
                ) {
                    SegmentedToggle(
                        options = themeModeOptions,
                        selectedIndex = selectedThemeIndex,
                        onSelect = { index ->
                            val mode = when (index) {
                                0 -> ThemeMode.LIGHT
                                1 -> ThemeMode.DARK
                                else -> ThemeMode.AUTO
                            }
                            themeViewModel.setThemeMode(mode)
                        },
                        contentDescriptionPrefix = stringResource(R.string.settings_theme_title),
                    )
                }
            }

            // Future settings groups will be added here:
            // - Playback settings (autoplay, etc.)
            // - Notifications settings
            // - About section
        }
    }
}
