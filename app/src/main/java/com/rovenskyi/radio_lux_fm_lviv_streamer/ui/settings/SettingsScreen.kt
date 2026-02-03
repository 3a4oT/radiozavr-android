package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.BuildConfig
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.LanguageRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RiddleSettingsViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.SettingsViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.ThemeViewModel
import com.rovenskyi.radiolux.core.components.settings.SettingsGroup
import com.rovenskyi.radiolux.core.components.settings.SettingsRow
import com.rovenskyi.radiolux.core.models.language.LanguageMode
import com.rovenskyi.radiolux.core.models.riddle.RiddleInterval
import com.rovenskyi.radiolux.core.models.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UnusedParameter")
fun SettingsScreen(
    languageRepository: LanguageRepository,
    onBackClick: () -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onPlaybackClick: () -> Unit,
    onRiddleClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    riddleSettingsViewModel: RiddleSettingsViewModel = hiltViewModel(),
) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    val riddleInterval by riddleSettingsViewModel.riddleInterval.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
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
            AppearanceGroup(themeMode = themeMode, onThemeClick = onThemeClick)
            LanguageGroup(languageRepository = languageRepository, onLanguageClick = onLanguageClick)
            PlaybackGroup(onPlaybackClick = onPlaybackClick)
            RiddleGroup(riddleInterval = riddleInterval, onRiddleClick = onRiddleClick)
            AboutGroup(onAboutClick = onAboutClick)
        }
    }
}

@Composable
private fun AppearanceGroup(themeMode: ThemeMode, onThemeClick: () -> Unit) {
    val currentThemeLabel = when (themeMode) {
        ThemeMode.LIGHT -> stringResource(R.string.settings_theme_light)
        ThemeMode.DARK -> stringResource(R.string.settings_theme_dark)
        ThemeMode.AUTO -> stringResource(R.string.settings_theme_auto)
    }

    SettingsGroup(title = stringResource(R.string.settings_group_appearance)) {
        SettingsRow(
            title = stringResource(R.string.settings_theme_title),
            subtitle = stringResource(R.string.settings_theme_subtitle),
            onClick = onThemeClick,
            contentDescription = stringResource(R.string.settings_theme_content_description, currentThemeLabel),
        ) {
            SettingsValueWithArrow(value = currentThemeLabel)
        }
    }
}

@Composable
private fun LanguageGroup(languageRepository: LanguageRepository, onLanguageClick: () -> Unit) {
    val currentLanguageMode = languageRepository.getLanguageMode()
    val currentLanguageLabel = when (currentLanguageMode) {
        LanguageMode.SYSTEM -> stringResource(R.string.settings_language_system)
        LanguageMode.UKRAINIAN -> stringResource(R.string.settings_language_ukrainian)
        LanguageMode.ENGLISH -> stringResource(R.string.settings_language_english)
    }

    SettingsGroup(title = stringResource(R.string.settings_group_language)) {
        SettingsRow(
            title = stringResource(R.string.settings_language_title),
            subtitle = stringResource(R.string.settings_language_subtitle),
            onClick = onLanguageClick,
            contentDescription = stringResource(R.string.settings_language_content_description, currentLanguageLabel),
        ) {
            SettingsValueWithArrow(value = currentLanguageLabel)
        }
    }
}

@Composable
private fun PlaybackGroup(onPlaybackClick: () -> Unit) {
    SettingsGroup(title = stringResource(R.string.settings_group_playback)) {
        SettingsRow(
            title = stringResource(R.string.settings_playback_title),
            subtitle = stringResource(R.string.settings_playback_subtitle),
            onClick = onPlaybackClick,
            contentDescription = stringResource(R.string.settings_playback_content_description),
        ) {
            SettingsValueWithArrow()
        }
    }
}

@Composable
private fun RiddleGroup(riddleInterval: RiddleInterval, onRiddleClick: () -> Unit) {
    val currentIntervalLabel = when (riddleInterval) {
        RiddleInterval.SECONDS_6 -> stringResource(R.string.settings_riddle_interval_6)
        RiddleInterval.SECONDS_12 -> stringResource(R.string.settings_riddle_interval_12)
        RiddleInterval.SECONDS_20 -> stringResource(R.string.settings_riddle_interval_20)
        RiddleInterval.SECONDS_30 -> stringResource(R.string.settings_riddle_interval_30)
        RiddleInterval.SECONDS_60 -> stringResource(R.string.settings_riddle_interval_60)
    }

    SettingsGroup(title = stringResource(R.string.settings_group_riddle)) {
        SettingsRow(
            title = stringResource(R.string.settings_riddle_title),
            subtitle = stringResource(R.string.settings_riddle_subtitle),
            onClick = onRiddleClick,
            contentDescription = stringResource(R.string.settings_riddle_content_description, currentIntervalLabel),
        ) {
            SettingsValueWithArrow(value = currentIntervalLabel)
        }
    }
}

@Composable
private fun AboutGroup(onAboutClick: () -> Unit) {
    val buildTypeLabel = if (BuildConfig.DEBUG) {
        stringResource(R.string.about_build_debug)
    } else {
        stringResource(R.string.about_build_release)
    }
    val aboutValue = "${BuildConfig.VERSION_NAME} $buildTypeLabel"

    SettingsGroup(title = stringResource(R.string.about_title)) {
        SettingsRow(
            title = stringResource(R.string.about_row_title),
            subtitle = stringResource(R.string.about_subtitle),
            onClick = onAboutClick,
            contentDescription = stringResource(R.string.about_content_description),
        ) {
            SettingsValueWithArrow(value = aboutValue)
        }
    }
}

@Composable
private fun SettingsValueWithArrow(value: String = "") {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
