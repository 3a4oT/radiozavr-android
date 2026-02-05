package com.rovenskyi.radio_lux_fm_lviv_streamer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.LanguageRepository
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.main.RadioPlayerScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings.AboutScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings.LanguageScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings.PlaybackSettingsScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings.RiddleWidgetSettingsScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings.SettingsScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings.ThemeScreen
import com.rovenskyi.radio_lux_fm_lviv_streamer.ui.settings.WidgetsSettingsScreen

/**
 * Main navigation host for the app.
 * Defines all navigation routes and their corresponding screens.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    languageRepository: LanguageRepository,
    modifier: Modifier = Modifier,
    onRequestAudioPermission: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = RadioPlayer,
        modifier = modifier,
    ) {
        composable<RadioPlayer> {
            RadioPlayerScreen(
                onSettingsClick = { navController.navigate(Settings) },
                onRequestAudioPermission = onRequestAudioPermission,
            )
        }
        composable<Settings> {
            SettingsScreen(
                languageRepository = languageRepository,
                onBackClick = { navController.popBackStack() },
                onThemeClick = { navController.navigate(Theme) },
                onLanguageClick = { navController.navigate(Language) },
                onPlaybackClick = { navController.navigate(Playback) },
                onWidgetsClick = { navController.navigate(WidgetsSettings) },
                onAboutClick = { navController.navigate(About) },
            )
        }
        composable<Theme> {
            ThemeScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable<Language> {
            LanguageScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable<Playback> {
            PlaybackSettingsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable<WidgetsSettings> {
            WidgetsSettingsScreen(
                onBackClick = { navController.popBackStack() },
                onRiddleClick = { navController.navigate(RiddleWidgetSettings) },
            )
        }
        composable<RiddleWidgetSettings> {
            RiddleWidgetSettingsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable<About> {
            AboutScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
