package com.rovenskyi.radiozavr.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rovenskyi.radiozavr.ui.settings.AboutScreen
import com.rovenskyi.radiozavr.ui.settings.LanguageScreen
import com.rovenskyi.radiozavr.ui.settings.PlaybackSettingsScreen
import com.rovenskyi.radiozavr.ui.settings.RiddleWidgetSettingsScreen
import com.rovenskyi.radiozavr.ui.settings.SettingsScreen
import com.rovenskyi.radiozavr.ui.settings.ThemeScreen
import com.rovenskyi.radiozavr.ui.settings.WidgetsSettingsScreen

/**
 * Settings navigation graph: settings hub and all sub-screens.
 */
fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    composable<Settings> {
        SettingsScreen(
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
