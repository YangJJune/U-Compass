package com.example.composesensor

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composesensor.locate.LocationRoute
import com.example.composesensor.locate.NaverMapRoute
import com.example.composesensor.screen.HomeScreen
import com.example.composesensor.screen.SensorRoute
import com.example.composesensor.locate.FingerprintingRoute
import com.example.composesensor.locate.TriangulationRoute
import com.example.composesensor.locate.InsNavigationRoute

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
                navigateToLocation = { navController.navigate(Route.Location) },
                navigateToNaverMap = { navController.navigate(Route.NaverMap) },
                navigateToFingerprinting = { navController.navigate(Route.Fingerprinting) },
                navigateToTriangulation = { navController.navigate(Route.Triangulation) },
                navigateToInsNavigation = { navController.navigate(Route.InsNavigation) }
            )
        }
        composable<Route.Sensor> {
            SensorRoute(padding = padding)
        }
        composable<Route.Location> {
            LocationRoute(padding = padding)
        }
        composable<Route.NaverMap> {
            NaverMapRoute(padding = padding)
        }
        composable<Route.Fingerprinting> {
            FingerprintingRoute(padding = padding)
        }
        composable<Route.Triangulation> {
            TriangulationRoute(padding = padding)
        }
        composable<Route.InsNavigation> {
            InsNavigationRoute(padding = padding)
        }
    }
}