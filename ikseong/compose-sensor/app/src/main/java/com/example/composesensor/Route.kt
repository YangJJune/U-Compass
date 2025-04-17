package com.example.composesensor

import kotlinx.serialization.Serializable


sealed class Route {

    @Serializable
    data object Home: Route()
    @Serializable
    data object Sensor: Route()
    @Serializable
    data object Location: Route()
    @Serializable
    data object NaverMap : Route()
    
    // 새로 추가된 경로
    @Serializable
    data object Fingerprinting : Route()
    @Serializable
    data object Triangulation : Route()
    @Serializable
    data object InsNavigation : Route() // INS 기반 실내 내비게이션
}