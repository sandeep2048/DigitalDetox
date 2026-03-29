package com.sanson.digitaldetox.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Dashboard : Screen("dashboard")
    data object Apps : Screen("apps")
    data object Messages : Screen("messages")
    data object Settings : Screen("settings")
}
