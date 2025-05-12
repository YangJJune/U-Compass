package com.ikseong.ucompass.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ikseong.ucompass.ui.create.navgraph.createNavGraph
import com.ikseong.ucompass.ui.create.viewmodel.CreateViewModel
import com.ikseong.ucompass.ui.main.screen.MainRoute
import com.ikseong.ucompass.ui.room.screen.RoomRoute

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
            MainRoute(
                padding = padding,
                navigateToRoom = { navController.navigate(Routes.Room(it)) },
                navigateToCreateRoom = { navController.navigate(Routes.CreateRoomTitle) },
            )
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
        composable<Routes.Room> { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<Routes.Room>()
            RoomRoute(
                id = route.id,
                padding = padding,
                navigateBack = { navController.popBackStack() },
            )

        }
    }
}