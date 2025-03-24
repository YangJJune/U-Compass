package com.example.composenavigation.nested

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.composenavigation.nested.screen.NestedHomeRoute
import com.example.composenavigation.nested.screen.NestedLandingRoute
import com.example.composenavigation.nested.screen.NestedLoginRoute
import com.example.composenavigation.nested.screen.NestedOnboardingRoute
import com.example.composenavigation.simple.Simple
import com.example.composenavigation.simple.screen.SimpleProfileRoute

@Composable
fun NestedNavHost(
    padding: PaddingValues,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Simple.Home) {
        composable<Simple.Home> {
            NestedHomeRoute(
                padding = padding,
                navigateToProfile = { navController.navigate(route = Simple.Profile(it)) },
                navigateToNested = { navController.navigate(Nested.Onboarding) },
                navigateToLanding = { navController.navigate(Nested.Landing) }
            )
        }
        composable<Simple.Profile> { backStackEntry ->
            val profile = backStackEntry.toRoute<Simple.Profile>()
            SimpleProfileRoute(
                padding = padding,
                name = profile.name,
                navigateToHome = {
                    navController.navigate(Simple.Home) {
                        popUpTo(route = Simple.Home) { inclusive = true }
                    }
                }
            )
        }
        navigation<Nested>(startDestination = Nested.Onboarding) {
            composable<Nested.Onboarding> {
                NestedOnboardingRoute(
                    padding = padding,
                    navigateToLanding = { navController.navigate(Nested.Landing) },
                    navigateToLogin = { navController.navigate(Nested.Login) },
                    navigateToHome = {
                        navController.navigate(Simple.Home) {
                            popUpTo(route = Simple.Home) { inclusive = true }
                        }
                    }
                )
            }

            composable<Nested.Landing> {
                NestedLandingRoute(
                    padding = padding,
                    navigateToOnboarding = { navController.navigate(Nested.Onboarding) },
                    navigateToLogin = { navController.navigate(Nested.Login) },
                    navigateToHome = { navController.navigate(Simple.Home) }
                )
            }
            composable<Nested.Login> {
                NestedLoginRoute(
                    padding = padding,
                    navigateToLanding = { navController.navigate(Nested.Landing) },
                    navigateToOnboarding = { navController.navigate(Nested.Onboarding) },
                    navigateToHome = { navController.navigate(Simple.Home) }
                )
            }
        }
    }
}

