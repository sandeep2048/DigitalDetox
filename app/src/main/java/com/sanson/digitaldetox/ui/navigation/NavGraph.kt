package com.sanson.digitaldetox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sanson.digitaldetox.ui.screens.apps.AppSelectorScreen
import com.sanson.digitaldetox.ui.screens.dashboard.DashboardScreen
import com.sanson.digitaldetox.ui.screens.messages.MessageEditorScreen
import com.sanson.digitaldetox.ui.screens.onboarding.OnboardingScreen
import com.sanson.digitaldetox.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onOnboardingComplete: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = { message -> onOnboardingComplete(message) }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        composable(Screen.Apps.route) {
            AppSelectorScreen()
        }
        composable(Screen.Messages.route) {
            MessageEditorScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
