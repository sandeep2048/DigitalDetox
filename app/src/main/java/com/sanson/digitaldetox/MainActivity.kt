package com.sanson.digitaldetox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sanson.digitaldetox.data.db.AppDatabase
import com.sanson.digitaldetox.data.db.entity.CustomMessageEntity
import com.sanson.digitaldetox.data.repository.MessageRepository
import com.sanson.digitaldetox.service.OverlayService
import com.sanson.digitaldetox.ui.components.BottomNavBar
import com.sanson.digitaldetox.ui.navigation.NavGraph
import com.sanson.digitaldetox.ui.navigation.Screen
import com.sanson.digitaldetox.ui.theme.DigitalDetoxTheme
import com.sanson.digitaldetox.util.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = PreferenceManager(applicationContext)

        setContent {
            DigitalDetoxTheme(darkTheme = true) {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val hasCompletedOnboarding = remember {
                    mutableStateOf(runBlocking { prefs.hasCompletedOnboarding.first() })
                }

                val startDestination = if (hasCompletedOnboarding.value) {
                    Screen.Dashboard.route
                } else {
                    Screen.Onboarding.route
                }

                val showBottomNav = currentRoute in listOf(
                    Screen.Dashboard.route,
                    Screen.Apps.route,
                    Screen.Messages.route,
                    Screen.Settings.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomNav,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { screen ->
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Dashboard.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { padding ->
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        onOnboardingComplete = { message ->
                            scope.launch {
                                prefs.setOnboardingCompleted(true)
                                hasCompletedOnboarding.value = true

                                if (message.isNotBlank()) {
                                    val db = AppDatabase.getInstance(applicationContext)
                                    val messageRepo = MessageRepository(db.customMessageDao())
                                    messageRepo.addMessage(message)
                                }

                                startForegroundService(
                                    Intent(this@MainActivity, OverlayService::class.java)
                                )

                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
