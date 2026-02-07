package com.rovenskyi.radiozavr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.rovenskyi.radiozavr.core.theme.RadiozavrTheme
import com.rovenskyi.radiozavr.data.radio.MediaControllerManager
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.audio.AudioVisualizerRepository
import com.rovenskyi.radiozavr.domain.platform.PlatformRepository
import com.rovenskyi.radiozavr.navigation.AppNavigation
import com.rovenskyi.radiozavr.service.RadioService
import com.rovenskyi.radiozavr.ui.settings.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var platformRepository: PlatformRepository

    @Inject
    lateinit var audioVisualizerRepository: AudioVisualizerRepository

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var mediaControllerManager: MediaControllerManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            permissionDeniedCallback?.invoke()
        }
    }

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Restart visualizer capture to use real audio data
            audioVisualizerRepository.restartCapture()
        }
    }

    private var permissionDeniedCallback: (() -> Unit)? = null
    private var audioPermissionRationaleCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash screen visible until theme is loaded from DataStore
        splashScreen.setKeepOnScreenCondition {
            !themeViewModel.isThemeLoaded.value
        }

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()
            val navController = rememberNavController()
            var showPermissionDialog by remember { mutableStateOf(false) }
            var showAudioPermissionRationale by remember { mutableStateOf(false) }
            var hasRequestedAudioPermission by remember { mutableStateOf(false) }

            // Set callback to update composable state
            permissionDeniedCallback = { showPermissionDialog = true }
            audioPermissionRationaleCallback = { showAudioPermissionRationale = true }

            RadiozavrTheme(themeMode = themeMode, isTv = platformRepository.isTv) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        navController = navController,
                        onRequestAudioPermission = {
                            if (!hasRequestedAudioPermission) {
                                hasRequestedAudioPermission = true
                                requestAudioPermissionIfNeeded()
                            }
                        },
                    )
                    if (showPermissionDialog) {
                        PermissionDeniedDialog(
                            onDismiss = { showPermissionDialog = false },
                            onOpenSettings = {
                                showPermissionDialog = false
                                openAppSettings()
                            },
                        )
                    }
                    if (showAudioPermissionRationale) {
                        AudioPermissionRationaleDialog(
                            onDismiss = { showAudioPermissionRationale = false },
                            onGrant = {
                                showAudioPermissionRationale = false
                                requestAudioPermissionLauncher.launch(
                                    Manifest.permission.RECORD_AUDIO,
                                )
                            },
                        )
                    }
                }
            }
        }
        // Connect to MediaSession for playback control and automatic notifications
        mediaControllerManager.connect()

        requestNotificationPermissionIfNeeded()
        setupAnalytics()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaControllerManager.release()
    }

    private fun setupAnalytics() {
        // Log app open event
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        // Set user properties for segmentation
        val deviceType = platformRepository.deviceType
        analyticsTracker.setUserProperty("device_type", deviceType.analyticsValue)
        analyticsTracker.setUserProperty("is_large_screen", platformRepository.isLargeScreen.toString())

        // Set Crashlytics context
        analyticsTracker.setErrorContext("device_type", deviceType.analyticsValue)
        analyticsTracker.setErrorContext("is_large_screen", platformRepository.isLargeScreen.toString())
    }

    override fun onStart() {
        super.onStart()
        // Notify service that app is in foreground (cancels auto-stop, may resume playback)
        startService(RadioService.createAppForegroundIntent(this))
    }

    override fun onStop() {
        super.onStop()
        // Notify service that app went to background (may trigger auto-stop on TV)
        startService(RadioService.createAppBackgroundIntent(this))
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun requestAudioPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.RECORD_AUDIO,
                    )
                ) {
                    audioPermissionRationaleCallback?.invoke()
                } else {
                    requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

@Composable
private fun PermissionDeniedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.permission_denied_title)) },
        text = { Text(text = stringResource(id = R.string.permission_denied_message)) },
        confirmButton = {
            Button(onClick = onOpenSettings) {
                Text(text = stringResource(id = R.string.open_settings))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
    )
}

@Composable
private fun AudioPermissionRationaleDialog(
    onDismiss: () -> Unit,
    onGrant: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.audio_permission_rationale_title)) },
        text = { Text(text = stringResource(id = R.string.audio_permission_rationale_message)) },
        confirmButton = {
            Button(onClick = onGrant) {
                Text(text = stringResource(id = R.string.grant_permission))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
    )
}
