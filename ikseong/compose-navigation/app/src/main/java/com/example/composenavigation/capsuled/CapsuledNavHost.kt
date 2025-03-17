package com.example.composenavigation.capsuled

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.composenavigation.capsuled.screen.CapsuledHomeRoute
import com.example.composenavigation.simple.Simple
import com.example.composenavigation.simple.screen.SimpleProfileRoute

@Composable
fun CapsuledNavHost(
    padding: PaddingValues,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Simple.Home) {
        composable<Simple.Home> {
            CapsuledHomeRoute(
                padding = padding,
                navigateToProfile = { navController.navigate(route = Simple.Profile(it)) },
                navigateToCapsuled = { navController.navigate(Capsuled.Onboarding) },
                navigateToLanding = { navController.navigate(Capsuled.Landing) }
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
        capsuledNavGraph(
            padding = padding,
            navigateToLanding = { navController.navigate(Capsuled.Landing) },
            navigateToLogin = { navController.navigate(Capsuled.Login) },
            navigateToHome = { navController.navigate(Simple.Home) }
        )
    }
}

