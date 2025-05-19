package com.ikseong.ucompass.data.network.response

import kotlinx.serialization.SerialName

data class CreateRoomResponse(
    @SerialName("title")
    val title: String,
    @SerialName("creator")
    val creator: String,
)
