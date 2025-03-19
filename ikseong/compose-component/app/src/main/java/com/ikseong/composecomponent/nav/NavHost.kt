package com.ikseong.composecomponent.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ikseong.composecomponent.MyBottomSheetScaffold
import com.ikseong.composecomponent.ui.theme.LargeTopAppBarExample

@Composable
fun MyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.TopAppBar.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.TopAppBar.route) {
            LargeTopAppBarExample()
        }
        composable(BottomNavItem.BottomSheet.route) {
            MyBottomSheetScaffold()
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("홈 화면")
    }
}

@Composable
fun FavoriteScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("즐겨찾기 화면")
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("프로필 화면")
    }
}