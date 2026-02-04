package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.LanguageViewModel
import com.rovenskyi.radiolux.core.components.settings.SelectableRow
import com.rovenskyi.radiolux.core.components.settings.SettingsGroup
import com.rovenskyi.radiolux.core.models.language.LanguageMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = hiltViewModel(),
) {
    val selectedMode by viewModel.selectedMode.collectAsState()

    val languageOptions = listOf(
        LanguageMode.SYSTEM to stringResource(R.string.settings_language_system),
        LanguageMode.UKRAINIAN to stringResource(R.string.settings_language_ukrainian),
        LanguageMode.ENGLISH to stringResource(R.string.settings_language_english),
    )

    SettingsThemeProvider {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.settings_language_title))
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
                    .padding(paddingValues),
            ) {
                SettingsGroup(
                    title = stringResource(R.string.settings_group_language),
                ) {
                    languageOptions.forEach { (mode, label) ->
                        SelectableRow(
                            text = label,
                            selected = selectedMode == mode,
                            onClick = { viewModel.setLanguageMode(mode) },
                            contentDescription = stringResource(
                                R.string.settings_language_content_description,
                                label,
                            ),
                        )
                    }
                }
            }
        }
    }
}
