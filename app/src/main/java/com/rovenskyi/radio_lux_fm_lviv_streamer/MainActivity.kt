package com.rovenskyi.radio_lux_fm_lviv_streamer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rovenskyi.radiolux.core.theme.RadioLuxTheme
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.RadioPlayerScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.RadioPlayerViewModel
import com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val radioPlayerViewModel: RadioPlayerViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    private var showDialog by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Trigger the dialog visibility state
            showDialog = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()

            RadioLuxTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RadioPlayerScreen(viewModel = radioPlayerViewModel)
                    if (showDialog) {
                        ShowPermissionDeniedDialog()
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh UI state when the app comes to the foreground
        radioPlayerViewModel.refreshState()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            // Show an explanation to the user why the permission is needed.
            // After the user sees the explanation, try again to request the permission.
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // No explanation needed; request the permission
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    private fun ShowPermissionDeniedDialog() {
        var showDialog by remember { mutableStateOf(true) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = stringResource(id = R.string.permission_denied_title)) },
                text = { Text(text = stringResource(id = R.string.permission_denied_message)) },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                        }
                        startActivity(intent)
                    }) {
                        Text(text = stringResource(id = R.string.open_settings))
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}
