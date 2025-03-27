package com.example.composeanimation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeanimation.screen.modifier.AnimateContentSizeScreen
import com.example.composeanimation.screen.composable.AnimatedContentScreen
import com.example.composeanimation.screen.composable.AnimatedVisibilityScreen
import com.example.composeanimation.screen.composable.CrossfadeScreen
import com.example.composeanimation.screen.HomeScreen
import com.example.composeanimation.screen.SpringScreen
import com.example.composeanimation.screen.TextAnimationScreen
import com.example.composeanimation.screen.modifier.AnimateOffsetScreen
import com.example.composeanimation.screen.modifier.AnimatePaddingScreen

@Composable
fun MainNavHost(
    innerPadding: PaddingValues,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> {
            HomeScreen(
                innerPadding = innerPadding,
                navigateToNavigate = { navController.navigate(Route.Navigate) },
                navigateToAnimatedVisibility = { navController.navigate(Route.AnimatedVisibility) },
                navigateToAnimatedContent = { navController.navigate(Route.AnimatedContent) },
                navigateToCrossfade = { navController.navigate(Route.Crossfade) },
                navigateToAnimateContentSize = { navController.navigate(Route.AnimateContentSize) },
                navigateToAnimateOffset = { navController.navigate(Route.AnimateOffset) },
                navigateToAnimatePadding = { navController.navigate(Route.AnimatePadding) },
                navigateToTextAnimation = { navController.navigate(Route.TextAnimation) },
                navigateToSpring = { navController.navigate(Route.Spring) }
            )
        }
        composable<Route.Navigate>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            Text("Navigate")
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
        composable<Route.Spring> {
            SpringScreen(
                innerPadding = innerPadding,
                navigateBack = navController::popBackStack
            )
        }
    }
}
