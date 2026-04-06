package com.rovenskyi.radiozavr.data.permission

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.permissionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "permission_prefs",
)

/**
 * Tracks app launch count and determines when it is appropriate to request runtime permissions.
 * Defers dialogs to later launches to avoid overwhelming new users.
 */
@Singleton
class PermissionLaunchGate @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val launchCountKey = intPreferencesKey("launch_count")

    val isNotificationPermissionEligible: Flow<Boolean> = context.permissionDataStore.data.map { prefs ->
        (prefs[launchCountKey] ?: 0) >= NOTIFICATION_PERMISSION_MIN_LAUNCH
    }

    val isAudioPermissionEligible: Flow<Boolean> = context.permissionDataStore.data.map { prefs ->
        (prefs[launchCountKey] ?: 0) >= AUDIO_PERMISSION_MIN_LAUNCH
    }

    suspend fun recordAppLaunch() {
        context.permissionDataStore.edit { prefs ->
            prefs[launchCountKey] = (prefs[launchCountKey] ?: 0) + 1
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_MIN_LAUNCH = 5
        private const val AUDIO_PERMISSION_MIN_LAUNCH = 8
    }
}
