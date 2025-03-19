package com.ikseong.composecomponent.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object TopAppBar : BottomNavItem(
        route = "TopAppBar",
        title = "TopAppBar",
        icon = Icons.Default.Home
    )

    object BottomSheet : BottomNavItem(
        route = "BottomSheet",
        title = "BottomSheet",
        icon = Icons.Default.Favorite
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "프로필",
        icon = Icons.Default.Person
    )
}