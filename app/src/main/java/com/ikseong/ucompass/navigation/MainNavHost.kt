package com.ikseong.ucompass.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ikseong.ucompass.create.navgraph.createNavGraph

@Composable
fun MainNavHost(
    padding: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable<Routes.Home> {

        }
        createNavGraph(
            navigateToFinished = { navController.navigate(Routes.CreateRoomFinished) },
            navigateToHome = { navController.navigate(Routes.Home) }
        )
        composable<Routes.Room> {

        }
    }
}