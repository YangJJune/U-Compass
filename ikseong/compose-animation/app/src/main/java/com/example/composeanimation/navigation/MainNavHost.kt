package com.example.composeanimation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeanimation.screen.AnimatedContentScreen
import com.example.composeanimation.screen.AnimatedVisibilityScreen
import com.example.composeanimation.screen.HomeScreen

@Composable
fun MainNavHost(
    innerPadding: PaddingValues,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> {
            HomeScreen(
                innerPadding = innerPadding,
                navigateToAnimatedVisibility = { navController.navigate(Route.AnimatedVisibility) },
                navigateToAnimatedContent = { navController.navigate(Route.AnimatedContent) }
            )
        }
        composable<Route.AnimatedVisibility> {
            AnimatedVisibilityScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
        composable<Route.AnimatedContent> {
            AnimatedContentScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
    }
}
