package com.rovenskyi.radiozavr.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rovenskyi.radiolux.core.models.riddle.RiddleAnswerMode
import com.rovenskyi.radiolux.core.models.riddle.RiddleInterval
import com.rovenskyi.radiozavr.domain.repository.PlatformRepository
import com.rovenskyi.radiozavr.domain.repository.PlaybackSettingsRepository
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
    private val riddleIntervalKey = intPreferencesKey("riddle_interval_seconds")
    private val riddleAnswerModeKey = stringPreferencesKey("riddle_answer_mode")

    override val autoPlayEnabled: Flow<Boolean> = context.playbackSettingsDataStore.data.map { prefs ->
        prefs[autoPlayKey] ?: platformRepository.isTv // Default: true on TV, false on Phone
    }

    override val autoStopOnBackgroundEnabled: Flow<Boolean> = context.playbackSettingsDataStore.data.map { prefs ->
        prefs[autoStopKey] ?: platformRepository.isTv // Default: true on TV, false on Phone
    }

    override val riddleInterval: Flow<RiddleInterval> = context.playbackSettingsDataStore.data.map { prefs ->
        val seconds = prefs[riddleIntervalKey] ?: RiddleInterval.DEFAULT.seconds
        RiddleInterval.fromSeconds(seconds)
    }

    override val riddleAnswerMode: Flow<RiddleAnswerMode> = context.playbackSettingsDataStore.data.map { prefs ->
        val modeName = prefs[riddleAnswerModeKey] ?: RiddleAnswerMode.DEFAULT.name
        runCatching { RiddleAnswerMode.valueOf(modeName) }.getOrDefault(RiddleAnswerMode.DEFAULT)
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

    override suspend fun setRiddleInterval(interval: RiddleInterval) {
        context.playbackSettingsDataStore.edit { prefs ->
            prefs[riddleIntervalKey] = interval.seconds
        }
    }

    override suspend fun setRiddleAnswerMode(mode: RiddleAnswerMode) {
        context.playbackSettingsDataStore.edit { prefs ->
            prefs[riddleAnswerModeKey] = mode.name
        }
    }
}
