package com.ikseong.ucompass.data.network.response


import com.ikseong.ucompass.data.network.response.RoomListResponse.RoomListResponseItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class RoomListResponse : ArrayList<RoomListResponseItem>(){
    @Serializable
    data class RoomListResponseItem(
        @SerialName("creator")
        val creator: String,
        @SerialName("id")
        val id: Int,
        @SerialName("participants")
        val participants: List<String>,
        @SerialName("title")
        val title: String
    )
}