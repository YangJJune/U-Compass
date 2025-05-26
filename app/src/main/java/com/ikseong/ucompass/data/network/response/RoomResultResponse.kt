package com.ikseong.ucompass.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomResultResponse(
    @SerialName("data")
    val data: Long,
    @SerialName("participants")
    val participants: List<String>,
)