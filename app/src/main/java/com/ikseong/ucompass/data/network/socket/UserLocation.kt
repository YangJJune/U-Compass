package com.ikseong.ucompass.data.network.socket

data class UserLocation(
    var lat: Double,
    var lng: Double,
    val name: String = "",
    val profileImageUrl : String = "",
)