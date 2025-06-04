package com.ikseong.ucompass.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomItemResponse(
    @SerialName("creator")
    val creator: String,
    @SerialName("id")
    val id: Long,
    @SerialName("participants")
    val participants: List<ParticipantInfoResponse>,
    @SerialName("title")
    val title: String
)

@Serializable
data class ParticipantInfoResponse(
    @SerialName("name")
    val name: String = "",
    @SerialName("email")
    val email : String = "",
    @SerialName("id")
    val id : Int = 0,
    @SerialName("deviceId")
    val deviceId: String = "",
    @SerialName("profileImage")
    val profileImage : String = "",
)