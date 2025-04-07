package com.ikseong.ucompass.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ikseong.ucompass.create.viewmodel.CreateViewModel
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
            paddingValues = padding,
            navigateToFinish = { navController.navigate(Routes.CreateRoomFinished) },
            navigateToHome = {
                navController.navigate(Routes.Home) {
                    popUpTo(Routes.Home) { inclusive = false }
                }
            },
            getBackStackCreateViewModel = { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(Routes.CreateRoomTitle)
                }
                hiltViewModel<CreateViewModel>(parentEntry)
            }
        )
        composable<Routes.Room> {

        }
    }
}