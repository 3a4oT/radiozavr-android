package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event

import com.rovenskyi.radiolux.core.models.language.LanguageMode
import com.rovenskyi.radiolux.core.models.theme.ThemeMode

sealed class SettingsEvent : AnalyticsEvent {

    data class ThemeChanged(val themeMode: ThemeMode) : SettingsEvent() {
        override val name: String = "theme_changed"
        override val params: Map<String, Any> = mapOf(PARAM_THEME_NAME to themeMode.name)
    }

    data class LanguageChanged(val languageMode: LanguageMode) : SettingsEvent() {
        override val name: String = "language_changed"
        override val params: Map<String, Any> = mapOf(PARAM_LANGUAGE to languageMode.name)
    }

    data class AutoPlayToggled(val enabled: Boolean) : SettingsEvent() {
        override val name: String = "auto_play_toggled"
        override val params: Map<String, Any> = mapOf(PARAM_ENABLED to enabled)
    }

    data class AutoStopToggled(val enabled: Boolean) : SettingsEvent() {
        override val name: String = "auto_stop_toggled"
        override val params: Map<String, Any> = mapOf(PARAM_ENABLED to enabled)
    }

    companion object {
        const val PARAM_THEME_NAME = "theme_name"
        const val PARAM_LANGUAGE = "language"
        const val PARAM_ENABLED = "enabled"
    }
}
