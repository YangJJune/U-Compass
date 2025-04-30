package com.ikseong.ucompass.ui.room.viewmodel

import com.ikseong.ucompass.ui.model.ParticipantInfo

data class RoomUiState(
    val address: String = "",
    val roomId: String = "",
    val roomName: String = "",
    val participantsNumber: Int = 0,
    val participantInfo: List<ParticipantInfo> = listOf(),
    val isHost: Boolean = false,
    val isSearchMode: Boolean = false,
    val isMapVisible: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val isRoomDeleteDialogVisible: Boolean = false
)




