package com.rovenskyi.radiozavr.domain.repository

import com.rovenskyi.radiozavr.core.models.permission.PermissionStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository for checking runtime permission statuses.
 * Provides reactive streams that update when permissions change.
 */
interface PermissionRepository {
    /**
     * Status of the POST_NOTIFICATIONS permission (Android 13+).
     * Returns GRANTED on pre-Android 13 devices.
     */
    val notificationPermissionStatus: Flow<PermissionStatus>

    /**
     * Status of the RECORD_AUDIO permission (for audio visualizer).
     * Returns GRANTED on pre-Android 9 devices.
     */
    val audioPermissionStatus: Flow<PermissionStatus>

    /**
     * Refreshes permission statuses.
     * Call this when returning to the app from system settings.
     */
    fun refreshPermissions()
}
