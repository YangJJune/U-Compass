package com.ikseong.ucompass.ui.room.viewmodel

import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.model.ParticipantInfo

data class RoomUiState(
    val address: String = "서울특별시 장안동",
    val roomId: String = "",
    val roomName: String = "위치 찾기 방1",
    val participantInfo: List<ParticipantInfo> = listOf(
        ParticipantInfo(
            name = "참가자1",
            profileUrl = "",
            direction = Direction.N,
            distance = 100,
            isShown = true,
        ),
        ParticipantInfo(
            name = "참가자2",
            profileUrl = "",
            direction = Direction.S,
            distance = 200,
            isShown = true,
        ),
        ParticipantInfo(
            name = "참가자3",
            profileUrl = "",
            direction = Direction.E,
            distance = 300,
            isShown = true,
        )
    ),
    val isHost: Boolean = false,
    val isSearchMode: Boolean = false,
    val isMapVisible: Boolean = false,
    val isRoomDeleteDialogVisible: Boolean = false
)




