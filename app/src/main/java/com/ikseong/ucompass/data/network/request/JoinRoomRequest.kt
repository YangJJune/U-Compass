package com.ikseong.ucompass.data.network.request

import kotlinx.serialization.SerialName

data class JoinRoomRequest(
    @SerialName("deviceId")
    val deviceId: String,
    @SerialName("roomId")
    val roomId: Int,
)
