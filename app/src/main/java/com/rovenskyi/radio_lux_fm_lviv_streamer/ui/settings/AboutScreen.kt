package com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.AboutViewModel
import com.rovenskyi.radiolux.core.components.qrcode.QrCode
import com.rovenskyi.radiolux.core.components.settings.PermissionStatusIndicator
import com.rovenskyi.radiolux.core.components.settings.SettingsGroup
import com.rovenskyi.radiolux.core.components.settings.SettingsRow
import com.rovenskyi.radiolux.core.models.permission.PermissionStatus
import com.rovenskyi.radiolux.core.theme.LocalDimensions
import com.rovenskyi.radiolux.core.theme.LocalIsTv

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

    SettingsThemeProvider {
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

/**
 * Source code row with platform-specific behavior:
 * - TV: Shows QR code (no browser available)
 * - Phone: Opens browser with safe intent handling
 */
@Composable
private fun SourceCodeRow(context: Context, onGitHubLinkClick: () -> Unit) {
    val isTv = LocalIsTv.current

    if (isTv) {
        SourceCodeQrRow()
    } else {
        SourceCodeLinkRow(context = context, onGitHubLinkClick = onGitHubLinkClick)
    }
}

/**
 * TV version: Shows QR code that can be scanned with phone.
 */
@Composable
private fun SourceCodeQrRow() {
    val dimensions = LocalDimensions.current

    SettingsRow(
        title = stringResource(R.string.about_source_code),
        subtitle = stringResource(R.string.about_source_code_qr_hint),
        contentDescription = stringResource(R.string.about_source_code_qr_content_description),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            QrCode(
                content = AboutViewModel.GITHUB_URL,
                size = dimensions.qrCodeSize,
                contentDescription = stringResource(R.string.about_source_code_qr_content_description),
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
            Text(
                text = stringResource(R.string.about_github_url),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Phone version: Clickable row that opens browser.
 * Handles case when no browser is available.
 */
@Composable
private fun SourceCodeLinkRow(context: Context, onGitHubLinkClick: () -> Unit) {
    val noBrowserMessage = stringResource(R.string.about_source_code_no_browser)

    SettingsRow(
        title = stringResource(R.string.about_source_code),
        subtitle = stringResource(R.string.about_github_url),
        onClick = {
            onGitHubLinkClick()
            openUrlSafely(context, AboutViewModel.GITHUB_URL, noBrowserMessage)
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

/**
 * Opens URL in browser with safe handling for devices without browser.
 */
private fun openUrlSafely(context: Context, url: String, errorMessage: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
