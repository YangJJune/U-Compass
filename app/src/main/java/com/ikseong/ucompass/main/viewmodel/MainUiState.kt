package com.ikseong.ucompass.main.viewmodel

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class MainUiState(
    val name: String = "",
    val address: String = "",
    val profileUrl: String = "",
    val roomList : PersistentList<RoomInfo> = persistentListOf(),
    val isPermissionDialogVisible: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false
)

data class RoomInfo(
    val roomId: Long = 0L,
    val roomName: String = "",
    val hostName: String = "",
    val roomLink: String = "",
    val isHost: Boolean = false
)