package com.ikseong.ucompass.data.network.request

import kotlinx.serialization.SerialName

data class CreateRoomRequest(
    @SerialName("title")
    val title: String,
    @SerialName("creator")
    val creator: String,
)
