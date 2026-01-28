package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

import com.rovenskyi.radiolux.core.models.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing theme settings.
 */
interface ThemeRepository {
    /**
     * Flow of current theme mode preference.
     */
    val themeMode: Flow<ThemeMode>

    /**
     * Update theme mode preference.
     */
    suspend fun setThemeMode(mode: ThemeMode)
}
