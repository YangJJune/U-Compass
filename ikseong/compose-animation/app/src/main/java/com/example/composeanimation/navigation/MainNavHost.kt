package com.example.composeanimation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeanimation.screen.modifier.AnimateContentSizeScreen
import com.example.composeanimation.screen.composable.AnimatedContentScreen
import com.example.composeanimation.screen.composable.AnimatedVisibilityScreen
import com.example.composeanimation.screen.composable.CrossfadeScreen
import com.example.composeanimation.screen.HomeScreen
import com.example.composeanimation.screen.TextAnimationScreen
import com.example.composeanimation.screen.modifier.AnimateOffsetScreen
import com.example.composeanimation.screen.modifier.AnimatePaddingScreen

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
                navigateToAnimatedContent = { navController.navigate(Route.AnimatedContent) },
                navigateToCrossfade = { navController.navigate(Route.Crossfade) },
                navigateToAnimateContentSize = { navController.navigate(Route.AnimateContentSize) },
                navigateToAnimateOffset = { navController.navigate(Route.AnimateOffset) },
                navigateToAnimatePadding = { navController.navigate(Route.AnimatePadding) },
                navigateToTextAnimation = { navController.navigate(Route.TextAnimation) }
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
        composable<Route.Crossfade> {
            CrossfadeScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
        composable<Route.AnimateContentSize> {
            AnimateContentSizeScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
        composable<Route.AnimateOffset> {
            AnimateOffsetScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
        composable<Route.AnimatePadding> {
            AnimatePaddingScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
        composable<Route.TextAnimation> {
            TextAnimationScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
    }
}
