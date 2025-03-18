package com.example.composenavigation.bottomnav

import kotlinx.serialization.Serializable

@Serializable
sealed class BottomNavRoute {
    @Serializable
    data object BottomHome : BottomNavRoute()

    @Serializable
    data object BottomAssets : BottomNavRoute()

    @Serializable
    data object BottomRecords : BottomNavRoute()

    @Serializable
    data object BottomHealth : BottomNavRoute()

    @Serializable
    data object BottomShopping : BottomNavRoute()
}