package com.rovenskyi.radiozavr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rovenskyi.radiozavr.ui.main.RadioPlayerScreen

/**
 * Main navigation host for the app.
 * Defines all navigation routes and their corresponding screens.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
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
        settingsNavGraph(navController)
    }
}
