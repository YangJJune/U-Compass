package com.example.composesensor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composesensor.locate.LocationRoute
import com.example.composesensor.screen.HomeScreen
import com.example.composesensor.screen.SensorRoute

@Composable
fun MainNavHost(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                paddingValues = padding,
                navigateToSensor = { navController.navigate(Route.Sensor) },
                navigateToLocation = { navController.navigate(Route.Location) }
            )
        }
        composable<Route.Sensor> {
            SensorRoute(padding = padding)
        }
        composable<Route.Location> {
            LocationRoute(padding = padding)
        }
    }
}