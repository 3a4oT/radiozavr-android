package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.model.ScreenName

sealed class ScreenEvent(
    val screenName: ScreenName,
    val screenClass: String,
) : AnalyticsEvent {

    override val name: String = "screen_view"

    override val params: Map<String, Any>
        get() = mapOf(
            PARAM_SCREEN_NAME to screenName.value,
            PARAM_SCREEN_CLASS to screenClass,
        )

    data object RadioPlayer : ScreenEvent(ScreenName.RADIO_PLAYER, "RadioPlayerScreen")
    data object Settings : ScreenEvent(ScreenName.SETTINGS, "SettingsScreen")
    data object Theme : ScreenEvent(ScreenName.THEME, "ThemeScreen")
    data object Language : ScreenEvent(ScreenName.LANGUAGE, "LanguageScreen")
    data object Playback : ScreenEvent(ScreenName.PLAYBACK, "PlaybackSettingsScreen")
    data object Riddle : ScreenEvent(ScreenName.RIDDLE, "RiddleSettingsScreen")
    data object About : ScreenEvent(ScreenName.ABOUT, "AboutScreen")

    companion object {
        const val PARAM_SCREEN_NAME = "screen_name"
        const val PARAM_SCREEN_CLASS = "screen_class"
    }
}
