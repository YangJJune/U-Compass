package com.example.composenavigation.simple

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.composenavigation.simple.screen.SimpleHomeRoute
import com.example.composenavigation.simple.screen.SimpleProfileRoute

@Composable
fun SimpleNavHost(
    padding: PaddingValues,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Simple.Home) {
        composable<Simple.Home> {
            SimpleHomeRoute(
                padding = padding,
                navigateToProfile = {
                    navController.navigate(route = Simple.Profile(it)) {
                        popUpTo(route = Simple.Home) { inclusive = true }
                    }
                })
        }
        composable<Simple.Profile> { backStackEntry ->
            val profile = backStackEntry.toRoute<Simple.Profile>()
            SimpleProfileRoute(
                padding = padding,
                name = profile.name,
                navigateToHome = { navController.navigate(Simple.Home) }
            )
        }
    }
}

