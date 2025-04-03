package com.example.composesensor


sealed class Route {

    data object Home: Route()
    data object Sensor: Route()
    data object Camera: Route()
}