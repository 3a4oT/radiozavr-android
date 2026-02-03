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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.PlaybackSettingsViewModel
import com.rovenskyi.radiolux.core.components.settings.SettingsGroup
import com.rovenskyi.radiolux.core.components.settings.SettingsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaybackSettingsViewModel = hiltViewModel(),
) {
    val autoPlayEnabled by viewModel.autoPlayEnabled.collectAsState()
    val autoStopEnabled by viewModel.autoStopOnBackgroundEnabled.collectAsState()
    val isTv = viewModel.isTv

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings_playback_title))
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
            SettingsGroup(
                title = stringResource(R.string.settings_group_playback),
            ) {
                // Auto-play on start (both TV and Phone)
                SettingsRow(
                    title = stringResource(R.string.settings_autoplay_title),
                    subtitle = stringResource(R.string.settings_autoplay_subtitle),
                    onClick = { viewModel.setAutoPlayEnabled(!autoPlayEnabled) },
                    contentDescription = stringResource(
                        R.string.settings_autoplay_content_description,
                        if (autoPlayEnabled) {
                            stringResource(R.string.settings_enabled)
                        } else {
                            stringResource(R.string.settings_disabled)
                        },
                    ),
                ) {
                    Switch(
                        checked = autoPlayEnabled,
                        onCheckedChange = { viewModel.setAutoPlayEnabled(it) },
                    )
                }

                // Auto-stop on background (TV only)
                if (isTv) {
                    SettingsRow(
                        title = stringResource(R.string.settings_autostop_title),
                        subtitle = stringResource(R.string.settings_autostop_subtitle),
                        onClick = { viewModel.setAutoStopOnBackgroundEnabled(!autoStopEnabled) },
                        contentDescription = stringResource(
                            R.string.settings_autostop_content_description,
                            if (autoStopEnabled) {
                                stringResource(R.string.settings_enabled)
                            } else {
                                stringResource(R.string.settings_disabled)
                            },
                        ),
                    ) {
                        Switch(
                            checked = autoStopEnabled,
                            onCheckedChange = { viewModel.setAutoStopOnBackgroundEnabled(it) },
                        )
                    }
                }
            }
        }
    }
}
