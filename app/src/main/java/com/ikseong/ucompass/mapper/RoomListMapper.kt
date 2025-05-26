package com.ikseong.ucompass.mapper

import com.ikseong.ucompass.data.network.response.RoomListResponse
import com.ikseong.ucompass.ui.main.viewmodel.RoomInfo
import kotlinx.collections.immutable.toPersistentList

fun RoomListResponse.toRoomInfo() =
    RoomInfo(
        roomId = this.id,
        roomName = this.title,
        hostName = this.creator,
        participantList = this.participants.toPersistentList(),
    )