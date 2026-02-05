package com.rovenskyi.radiozavr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenskyi.radiolux.core.models.permission.PermissionStatus
import com.rovenskyi.radiozavr.BuildConfig
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.ButtonEvent
import com.rovenskyi.radiozavr.domain.analytics.event.ScreenEvent
import com.rovenskyi.radiozavr.domain.analytics.model.ButtonName
import com.rovenskyi.radiozavr.domain.analytics.model.ScreenName
import com.rovenskyi.radiozavr.domain.repository.PermissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val permissionRepository: PermissionRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    val versionName: String = BuildConfig.VERSION_NAME
    val isDebugBuild: Boolean = BuildConfig.DEBUG

    init {
        analyticsTracker.track(ScreenEvent.About)
    }

    fun onGitHubLinkClicked() {
        analyticsTracker.track(ButtonEvent(ButtonName.GITHUB_LINK, ScreenName.ABOUT))
    }

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
