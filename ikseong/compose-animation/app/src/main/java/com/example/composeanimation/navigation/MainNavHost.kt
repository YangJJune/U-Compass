package com.example.composeanimation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeanimation.screen.HomeScreen

@Composable
fun MainNavHost(
    innerPadding : PaddingValues,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable<Route.Home> {
            HomeScreen(innerPadding)
        }
    }
}
