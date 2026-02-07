package com.rovenskyi.radiozavr.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rovenskyi.radiozavr.R
import com.rovenskyi.radiozavr.core.components.settings.SettingsGroup
import com.rovenskyi.radiozavr.core.components.settings.SettingsRow
import com.rovenskyi.radiozavr.viewmodel.WidgetsSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsSettingsScreen(
    onBackClick: () -> Unit,
    onRiddleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ViewModel instantiation triggers screen_view analytics in init{}
    hiltViewModel<WidgetsSettingsViewModel>()

    // Focus restoration: request focus on first row when returning to screen
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SettingsThemeProvider {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.settings_widgets_title))
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
                    title = stringResource(R.string.settings_group_widgets),
                ) {
                    SettingsRow(
                        title = stringResource(R.string.settings_riddle_widget_title),
                        subtitle = stringResource(R.string.settings_riddle_widget_subtitle),
                        onClick = onRiddleClick,
                        contentDescription = stringResource(R.string.settings_riddle_widget_content_description),
                        focusRequester = focusRequester,
                    ) {
                        WidgetValueWithArrow()
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetValueWithArrow() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
