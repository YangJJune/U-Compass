package com.ikseong.ucompass.data.network.socket.write

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocketLoginDto(
    @SerialName("type")
    val type: String = JSON_TYPE_LOGIN,
    @SerialName("user_id")
    val userId: String,
    @SerialName("room_id")
    val roomId: Int
) {
    private companion object {
        const val JSON_TYPE_LOGIN = "login"
    }
}