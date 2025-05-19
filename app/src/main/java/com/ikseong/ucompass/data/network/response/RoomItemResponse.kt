package com.ikseong.ucompass.data.network.response

import kotlinx.serialization.SerialName

data class RoomItemResponse(
    @SerialName("creator")
    val creator: String,
    @SerialName("id")
    val id: Long,
    @SerialName("participants")
    val participants: List<String>,
    @SerialName("title")
    val title: String
)