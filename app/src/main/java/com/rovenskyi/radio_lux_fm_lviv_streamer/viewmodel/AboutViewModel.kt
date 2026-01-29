package com.rovenskyi.radio_lux_fm_lviv_streamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radio_lux_fm_lviv_streamer.BuildConfig
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PermissionRepository
import com.rovenskyi.radiolux.core.models.permission.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val permissionRepository: PermissionRepository,
) : ViewModel() {

    val versionName: String = BuildConfig.VERSION_NAME
    val isDebugBuild: Boolean = BuildConfig.DEBUG

    val notificationPermissionStatus: StateFlow<PermissionStatus> =
        permissionRepository.notificationPermissionStatus
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PermissionStatus.DENIED,
            )

    val audioPermissionStatus: StateFlow<PermissionStatus> =
        permissionRepository.audioPermissionStatus
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PermissionStatus.DENIED,
            )

    fun refreshPermissions() {
        permissionRepository.refreshPermissions()
    }

    companion object {
        const val GITHUB_URL = "https://github.com/3a4oT/radio-lux-fm-lviv-android-streamer"
    }
}
