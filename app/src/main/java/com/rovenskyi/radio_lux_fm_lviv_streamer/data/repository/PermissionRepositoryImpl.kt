package com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PermissionRepository
import com.rovenskyi.radiolux.core.models.permission.PermissionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PermissionRepository {

    private val _notificationPermissionStatus = MutableStateFlow(checkNotificationPermission())
    override val notificationPermissionStatus: Flow<PermissionStatus> =
        _notificationPermissionStatus.asStateFlow()

    private val _audioPermissionStatus = MutableStateFlow(checkAudioPermission())
    override val audioPermissionStatus: Flow<PermissionStatus> =
        _audioPermissionStatus.asStateFlow()

    override fun refreshPermissions() {
        _notificationPermissionStatus.value = checkNotificationPermission()
        _audioPermissionStatus.value = checkAudioPermission()
    }

    private fun checkNotificationPermission(): PermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED
        } else {
            // Pre-Android 13: notifications don't require runtime permission
            PermissionStatus.GRANTED
        }
    }

    private fun checkAudioPermission(): PermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED
        } else {
            // Pre-Android 9: audio capture doesn't require runtime permission
            PermissionStatus.GRANTED
        }
    }
}
