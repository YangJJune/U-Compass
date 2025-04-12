package com.ikseong.ucompass.main.viewmodel

data class MainUiState(
    val name: String = "",
    val address: String = "",
    val profileUrl: String = "",
    val roomList : List<RoomInfo> = listOf()
)

data class RoomInfo(
    val roomId: Long = 0L,
    val roomName: String = "",
    val hostName: String = "",
    val roomLink: String = "",
    val isHost: Boolean = false
)