package com.ikseong.ucompass.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaveRoomRequest(
    @SerialName("deviceId")
    val deviceId: String,
    @SerialName("roomId")
    val roomId: Int,
)
