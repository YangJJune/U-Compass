package com.example.composesensor

import kotlinx.serialization.Serializable


sealed class Route {

    @Serializable
    data object Home: Route()
    @Serializable
    data object Sensor: Route()
    @Serializable
    data object Camera: Route()
}