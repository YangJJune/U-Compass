package com.ikseong.ucompass.data.network.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomItemResponse(
    @SerialName("creator")
    val creator: String,
    @SerialName("id")
    val id: Int,
    @SerialName("participants")
    val participants: List<Participant>,
    @SerialName("title")
    val title: String
) {
    @Serializable
    data class Participant(
        @SerialName("deviceId")
        val deviceId: String,
        @SerialName("email")
        val email: String? = null,
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String,
        @SerialName("profileImage")
        val profileImage: String? = null
    )
}