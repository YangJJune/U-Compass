package com.example.composenavigation.bottomnav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions

class BottomNavController(
    val navController: NavHostController
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val startDestination = BottomTab.HOME.route

    val currentTab: BottomTab?
        @Composable get() = BottomTab.entries.find { tab ->
            currentDestination?.route == tab.route::class.qualifiedName
        }

    fun navigate(tab: BottomTab) {
        val navOptions = navOptions {
            popUpTo(BottomTab.HOME.route) {
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            BottomTab.HOME -> navController.navigateToHome(navOptions)
            BottomTab.ASSETS -> navController.navigateToAssets(navOptions)
            BottomTab.RECORDS -> navController.navigateToRecords(navOptions)
            BottomTab.HEALTH -> navController.navigateToHealth(navOptions)
            BottomTab.SHOPPING -> navController.navigateToShopping(navOptions)
        }
    }

    fun NavController.navigateToHome(navOptions: NavOptions) {
        navController.navigate(BottomTab.HOME.route, navOptions)
    }

    fun NavController.navigateToAssets(navOptions: NavOptions) {
        navController.navigate(BottomTab.ASSETS.route, navOptions)
    }

    fun NavController.navigateToRecords(navOptions: NavOptions) {
        navController.navigate(BottomTab.RECORDS.route, navOptions)
    }

    fun NavController.navigateToHealth(navOptions: NavOptions) {
        navController.navigate(BottomTab.HEALTH.route, navOptions)
    }

    fun NavController.navigateToShopping(navOptions: NavOptions) {
        navController.navigate(BottomTab.SHOPPING.route, navOptions)
    }

    @Composable
    fun shouldShowBottomBar() = BottomTab.entries.any {
        currentDestination?.hasRoute(it.route::class) ?: false
    }

}

@Composable
fun rememberBottomNavigator(
    navController: NavHostController = rememberNavController(),
): BottomNavController = remember(navController) {
    BottomNavController(navController)
}
