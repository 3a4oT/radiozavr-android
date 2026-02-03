package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.AboutViewModel
import com.rovenskyi.radiolux.core.components.settings.PermissionStatusIndicator
import com.rovenskyi.radiolux.core.components.settings.SettingsGroup
import com.rovenskyi.radiolux.core.components.settings.SettingsRow
import com.rovenskyi.radiolux.core.models.permission.PermissionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AboutViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationStatus by viewModel.notificationPermissionStatus.collectAsState()
    val audioStatus by viewModel.audioPermissionStatus.collectAsState()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshPermissions()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.about_title)) },
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
            ApplicationInfoGroup(
                versionName = viewModel.versionName,
                isDebugBuild = viewModel.isDebugBuild,
                onGitHubLinkClick = viewModel::onGitHubLinkClicked,
            )
            PermissionsGroup(
                notificationStatus = notificationStatus,
                audioStatus = audioStatus,
            )
        }
    }
}

@Composable
private fun ApplicationInfoGroup(
    versionName: String,
    isDebugBuild: Boolean,
    onGitHubLinkClick: () -> Unit,
) {
    val context = LocalContext.current
    val buildTypeLabel = if (isDebugBuild) {
        stringResource(R.string.about_build_debug)
    } else {
        stringResource(R.string.about_build_release)
    }

    SettingsGroup(title = stringResource(R.string.about_group_application)) {
        SettingsRow(title = stringResource(R.string.about_version)) {
            SettingsValue(text = versionName)
        }
        SettingsRow(title = stringResource(R.string.about_build_type)) {
            SettingsValue(text = buildTypeLabel)
        }
        SettingsRow(title = stringResource(R.string.about_author)) {
            SettingsValue(text = stringResource(R.string.about_author_name))
        }
        SourceCodeRow(context = context, onGitHubLinkClick = onGitHubLinkClick)
    }
}

@Composable
private fun SourceCodeRow(context: Context, onGitHubLinkClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.about_source_code),
        subtitle = stringResource(R.string.about_github_url),
        onClick = {
            onGitHubLinkClick()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AboutViewModel.GITHUB_URL))
            context.startActivity(intent)
        },
        contentDescription = stringResource(R.string.about_source_code_content_description),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PermissionsGroup(
    notificationStatus: PermissionStatus,
    audioStatus: PermissionStatus,
) {
    val notificationStatusLabel = notificationStatus.toLocalizedLabel()
    val audioStatusLabel = audioStatus.toLocalizedLabel()

    SettingsGroup(title = stringResource(R.string.about_group_permissions)) {
        PermissionRow(
            title = stringResource(R.string.about_permission_notifications),
            subtitle = stringResource(R.string.about_permission_notifications_subtitle),
            status = notificationStatus,
            statusLabel = notificationStatusLabel,
        )
        PermissionRow(
            title = stringResource(R.string.about_permission_audio),
            subtitle = stringResource(R.string.about_permission_audio_subtitle),
            status = audioStatus,
            statusLabel = audioStatusLabel,
        )
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    status: PermissionStatus,
    statusLabel: String,
) {
    SettingsRow(
        title = title,
        subtitle = subtitle,
        contentDescription = stringResource(R.string.about_permission_content_description, statusLabel),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SettingsValue(text = statusLabel)
            Spacer(modifier = Modifier.width(8.dp))
            PermissionStatusIndicator(status = status, contentDescription = null)
        }
    }
}

@Composable
private fun SettingsValue(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun PermissionStatus.toLocalizedLabel(): String {
    return when (this) {
        PermissionStatus.GRANTED -> stringResource(R.string.about_permission_status_granted)
        PermissionStatus.DENIED -> stringResource(R.string.about_permission_status_denied)
        PermissionStatus.NOT_ASKED -> stringResource(R.string.about_permission_status_not_asked)
    }
}
