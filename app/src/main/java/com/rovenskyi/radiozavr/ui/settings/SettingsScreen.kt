package com.rovenskyi.radiozavr.ui.settings

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radiozavr.BuildConfig
import com.rovenskyi.radiozavr.R
import com.rovenskyi.radiozavr.core.components.settings.SettingsGroup
import com.rovenskyi.radiozavr.core.components.settings.SettingsRow
import com.rovenskyi.radiozavr.core.models.language.LanguageMode
import com.rovenskyi.radiozavr.core.models.theme.ThemeMode
import com.rovenskyi.radiozavr.domain.repository.LanguageRepository
import com.rovenskyi.radiozavr.viewmodel.SettingsViewModel
import com.rovenskyi.radiozavr.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    languageRepository: LanguageRepository,
    onBackClick: () -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onPlaybackClick: () -> Unit,
    onWidgetsClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    // ViewModel instantiation triggers screen_view analytics in init{}
    hiltViewModel<SettingsViewModel>()

    val themeMode by themeViewModel.themeMode.collectAsState()

    // Focus restoration: track last clicked row and restore focus on return
    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(0) }
    val focusRequesters = remember { List(SETTINGS_ROW_COUNT) { FocusRequester() } }

    LaunchedEffect(Unit) {
        focusRequesters.getOrNull(lastFocusedIndex)?.requestFocus()
    }

    SettingsThemeProvider {
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
                AppearanceGroup(
                    themeMode = themeMode,
                    onThemeClick = {
                        lastFocusedIndex = 0
                        onThemeClick()
                    },
                    focusRequester = focusRequesters[0],
                )
                LanguageGroup(
                    languageRepository = languageRepository,
                    onLanguageClick = {
                        lastFocusedIndex = 1
                        onLanguageClick()
                    },
                    focusRequester = focusRequesters[1],
                )
                PlaybackGroup(
                    onPlaybackClick = {
                        lastFocusedIndex = 2
                        onPlaybackClick()
                    },
                    focusRequester = focusRequesters[2],
                )
                WidgetsGroup(
                    onWidgetsClick = {
                        lastFocusedIndex = 3
                        onWidgetsClick()
                    },
                    focusRequester = focusRequesters[3],
                )
                AboutGroup(
                    onAboutClick = {
                        lastFocusedIndex = 4
                        onAboutClick()
                    },
                    focusRequester = focusRequesters[4],
                )
            }
        }
    }
}

private const val SETTINGS_ROW_COUNT = 5

@Composable
private fun AppearanceGroup(
    themeMode: ThemeMode,
    onThemeClick: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
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
            focusRequester = focusRequester,
        ) {
            SettingsValueWithArrow(value = currentThemeLabel)
        }
    }
}

@Composable
private fun LanguageGroup(
    languageRepository: LanguageRepository,
    onLanguageClick: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
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
            focusRequester = focusRequester,
        ) {
            SettingsValueWithArrow(value = currentLanguageLabel)
        }
    }
}

@Composable
private fun PlaybackGroup(
    onPlaybackClick: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
    SettingsGroup(title = stringResource(R.string.settings_group_playback)) {
        SettingsRow(
            title = stringResource(R.string.settings_playback_title),
            subtitle = stringResource(R.string.settings_playback_subtitle),
            onClick = onPlaybackClick,
            contentDescription = stringResource(R.string.settings_playback_content_description),
            focusRequester = focusRequester,
        ) {
            SettingsValueWithArrow()
        }
    }
}

@Composable
private fun WidgetsGroup(
    onWidgetsClick: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
    SettingsGroup(title = stringResource(R.string.settings_widgets_title)) {
        SettingsRow(
            title = stringResource(R.string.settings_widgets_row_title),
            subtitle = stringResource(R.string.settings_widgets_subtitle),
            onClick = onWidgetsClick,
            contentDescription = stringResource(R.string.settings_widgets_content_description),
            focusRequester = focusRequester,
        ) {
            SettingsValueWithArrow()
        }
    }
}

@Composable
private fun AboutGroup(
    onAboutClick: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
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
            focusRequester = focusRequester,
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
