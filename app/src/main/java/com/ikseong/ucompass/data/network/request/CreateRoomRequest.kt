package com.ikseong.ucompass.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomRequest(
    @SerialName("title")
    val title: String,
    @SerialName("creator")
    val creator: String,
)
