package com.rovenskyi.radiozavr.ui.settings

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radiozavr.R
import com.rovenskyi.radiozavr.core.components.settings.SelectableRow
import com.rovenskyi.radiozavr.core.components.settings.SettingsGroup
import com.rovenskyi.radiozavr.core.models.theme.ThemeMode
import com.rovenskyi.radiozavr.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val selectedMode by themeViewModel.themeMode.collectAsState()

    LaunchedEffect(Unit) {
        themeViewModel.trackThemeScreenView()
    }

    val themeOptions = listOf(
        ThemeMode.LIGHT to stringResource(R.string.settings_theme_light),
        ThemeMode.DARK to stringResource(R.string.settings_theme_dark),
        ThemeMode.AUTO to stringResource(R.string.settings_theme_auto),
    )

    SettingsThemeProvider {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.settings_theme_title))
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
                    title = stringResource(R.string.settings_group_appearance),
                ) {
                    themeOptions.forEach { (mode, label) ->
                        SelectableRow(
                            text = label,
                            selected = selectedMode == mode,
                            onClick = { themeViewModel.setThemeMode(mode) },
                            contentDescription = stringResource(
                                R.string.settings_theme_content_description,
                                label,
                            ),
                        )
                    }
                }
            }
        }
    }
}
