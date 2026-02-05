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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RiddleSettingsViewModel
import com.rovenskyi.radiolux.core.components.settings.SelectableRow
import com.rovenskyi.radiolux.core.components.settings.SettingsGroup
import com.rovenskyi.radiolux.core.models.riddle.RiddleAnswerMode
import com.rovenskyi.radiolux.core.models.riddle.RiddleInterval

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiddleWidgetSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RiddleSettingsViewModel = hiltViewModel(),
) {
    val selectedInterval by viewModel.riddleInterval.collectAsState()
    val selectedAnswerMode by viewModel.riddleAnswerMode.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.trackRiddleScreenView()
    }

    val intervalOptions = listOf(
        RiddleInterval.SECONDS_6 to stringResource(R.string.settings_riddle_interval_6),
        RiddleInterval.SECONDS_12 to stringResource(R.string.settings_riddle_interval_12),
        RiddleInterval.SECONDS_20 to stringResource(R.string.settings_riddle_interval_20),
        RiddleInterval.SECONDS_30 to stringResource(R.string.settings_riddle_interval_30),
        RiddleInterval.SECONDS_60 to stringResource(R.string.settings_riddle_interval_60),
    )

    val answerModeOptions = listOf(
        RiddleAnswerMode.AUTOMATIC to stringResource(R.string.settings_answer_mode_automatic),
        RiddleAnswerMode.MANUAL to stringResource(R.string.settings_answer_mode_manual),
    )

    SettingsThemeProvider {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.settings_riddle_widget_title))
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
                    title = stringResource(R.string.settings_riddle_answer_mode_group),
                ) {
                    answerModeOptions.forEach { (mode, label) ->
                        SelectableRow(
                            text = label,
                            selected = selectedAnswerMode == mode,
                            onClick = { viewModel.setRiddleAnswerMode(mode) },
                            contentDescription = stringResource(
                                R.string.settings_answer_mode_content_description,
                                label,
                            ),
                        )
                    }
                }

                SettingsGroup(
                    title = stringResource(R.string.settings_riddle_interval_group),
                ) {
                    intervalOptions.forEach { (interval, label) ->
                        SelectableRow(
                            text = label,
                            selected = selectedInterval == interval,
                            onClick = { viewModel.setRiddleInterval(interval) },
                            contentDescription = stringResource(
                                R.string.settings_riddle_interval_content_description,
                                label,
                            ),
                        )
                    }
                }
            }
        }
    }
}
