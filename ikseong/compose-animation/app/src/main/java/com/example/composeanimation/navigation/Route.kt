package com.example.composeanimation.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object Home : Route()
    @Serializable
    data object AnimatedVisibility : Route()
    @Serializable
    data object AnimatedContent : Route()
    @Serializable
    data object Crossfade : Route()
}