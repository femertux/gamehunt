package com.mobileni.gamehunt.presentation

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mobileni.gamehunt.presentation.detail.GameDetailRoute
import com.mobileni.gamehunt.presentation.home.HomeRoute
import com.mobileni.gamehunt.presentation.navigation.GameDetail
import com.mobileni.gamehunt.presentation.navigation.Home
import com.mobileni.gamehunt.presentation.ui.theme.GameHuntTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Shows the splash screen while the app is loading.
        installSplashScreen()

        // Enables drawing behind system bars for immersive experience.
        enableEdgeToEdge()
        setStatusBarLightIcons(window, useDarkIcons = false)

        setContent {
            GameHuntTheme {
                val navController = rememberNavController()

                // Sets up the navigation graph using Navigation Compose.
                NavHost(
                    navController = navController,
                    startDestination = Home
                ) {
                    // Home screen route with navigation callback to game detail screen.
                    composable<Home> {
                        HomeRoute(
                            onNavigateToGameDetail = { slug ->
                                navController.navigate(GameDetail(slug = slug))
                            }
                        )
                    }

                    // Game detail screen route using the game's slug from the navigation entry.
                    composable<GameDetail> { backStackEntry ->
                        val detail = backStackEntry.toRoute<GameDetail>()
                        GameDetailRoute(
                            slug = detail.slug,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    fun setStatusBarLightIcons(window: Window, useDarkIcons: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = useDarkIcons
    }
}

