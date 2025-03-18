package com.example.composenavigation.bottomnav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.composenavigation.bottomnav.screen.BottomAssetsRoute
import com.example.composenavigation.bottomnav.screen.BottomHealthRoute
import com.example.composenavigation.bottomnav.screen.BottomHomeRoute
import com.example.composenavigation.bottomnav.screen.BottomRecordsRoute
import com.example.composenavigation.bottomnav.screen.BottomShoppingRoute

@Composable
fun BottomNavHost(
    modifier: Modifier = Modifier,
    navigator: BottomNavController,
    padding: PaddingValues
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        composable<BottomNavRoute.BottomHome> {
            BottomHomeRoute(padding = padding)
        }
        composable<BottomNavRoute.BottomAssets> {
            BottomAssetsRoute(padding = padding)
        }
        composable<BottomNavRoute.BottomRecords> {
            BottomRecordsRoute(padding = padding)
        }
        composable<BottomNavRoute.BottomHealth> {
            BottomHealthRoute(padding = padding)
        }
        composable<BottomNavRoute.BottomShopping> {
            BottomShoppingRoute(padding = padding)
        }
    }
}