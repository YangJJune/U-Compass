package com.ikseong.ucompass.data.network.socket.write

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocketLocationWriteDto(
    @SerialName("type")
    val type: String = JSON_LOCATION_UPDATE,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lng")
    val lng: Double
) {
    private companion object {
        const val JSON_LOCATION_UPDATE = "location_update"
    }
}