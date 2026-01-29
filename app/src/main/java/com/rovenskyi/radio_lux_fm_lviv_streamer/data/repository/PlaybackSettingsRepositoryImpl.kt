package com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlatformRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.PlaybackSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.playbackSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "playback_settings",
)

@Singleton
class PlaybackSettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val platformRepository: PlatformRepository,
) : PlaybackSettingsRepository {

    private val autoPlayKey = booleanPreferencesKey("auto_play_enabled")
    private val autoStopKey = booleanPreferencesKey("auto_stop_on_background_enabled")

    override val autoPlayEnabled: Flow<Boolean> = context.playbackSettingsDataStore.data.map { prefs ->
        prefs[autoPlayKey] ?: platformRepository.isTv // Default: true on TV, false on Phone
    }

    override val autoStopOnBackgroundEnabled: Flow<Boolean> = context.playbackSettingsDataStore.data.map { prefs ->
        prefs[autoStopKey] ?: platformRepository.isTv // Default: true on TV, false on Phone
    }

    override suspend fun setAutoPlayEnabled(enabled: Boolean) {
        context.playbackSettingsDataStore.edit { prefs ->
            prefs[autoPlayKey] = enabled
        }
    }

    override suspend fun setAutoStopOnBackgroundEnabled(enabled: Boolean) {
        context.playbackSettingsDataStore.edit { prefs ->
            prefs[autoStopKey] = enabled
        }
    }
}
