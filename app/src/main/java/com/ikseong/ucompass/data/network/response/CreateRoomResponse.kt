package com.ikseong.ucompass.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomResponse(
    @SerialName("title")
    val title: String,
    @SerialName("creator")
    val creator: String,
)
