package com.example.composenavigation.capsuled

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.composenavigation.capsuled.screen.CapsuledLandingRoute
import com.example.composenavigation.capsuled.screen.CapsuledLoginRoute
import com.example.composenavigation.capsuled.screen.CapsuledOnboardingRoute

fun NavGraphBuilder.capsuledNavGraph(
    padding: PaddingValues,
    navigateToLanding: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable<Capsuled.Landing> {
        CapsuledLandingRoute(
            padding = padding,
            navigateToOnboarding = navigateToLanding,
            navigateToLogin = navigateToLogin,
            navigateToHome = navigateToHome
        )
    }
    composable<Capsuled.Login> {
        CapsuledLoginRoute(
            padding = padding,
            navigateToLanding = navigateToLanding,
            navigateToOnboarding = navigateToLanding,
            navigateToHome = navigateToHome
        )
    }
    composable<Capsuled.Onboarding> {
        CapsuledOnboardingRoute(
            padding = padding,
            navigateToLanding = navigateToLanding,
            navigateToLogin = navigateToLogin,
            navigateToHome = navigateToHome
        )
    }
}