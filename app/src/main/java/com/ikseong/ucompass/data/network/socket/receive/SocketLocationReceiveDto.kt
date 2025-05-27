package com.ikseong.ucompass.data.network.socket.receive

import kotlinx.serialization.SerialName

data class SocketLocationReceiveDto(
    @SerialName("type")
    val type: String = JSON_LOCATION_RECEIVE,
    @SerialName("user_id")
    val deviceId: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lng")
    val lng: Double
) {
    companion object {
        const val JSON_LOCATION_RECEIVE = "location_broadcast"
    }
}
