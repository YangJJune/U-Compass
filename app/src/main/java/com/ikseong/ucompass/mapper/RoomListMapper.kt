package com.ikseong.ucompass.mapper

import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.ui.main.viewmodel.RoomInfo
import kotlinx.collections.immutable.toPersistentList

fun RoomItemResponse.toRoomInfo() =
    RoomInfo(
        roomId = this.id.toLong(),
        roomName = this.title,
        hostName = this.creator,
        participantList = this.participants.map { it.name }.toPersistentList(),
    )