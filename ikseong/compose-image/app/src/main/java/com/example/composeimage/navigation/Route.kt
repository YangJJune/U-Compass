package com.example.composeimage.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object DefaultUrl : Route()
    @Serializable
    data object RemoteUrl : Route()
}