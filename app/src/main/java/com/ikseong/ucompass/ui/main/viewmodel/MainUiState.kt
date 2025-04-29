package com.ikseong.ucompass.ui.main.viewmodel

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class MainUiState(
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val profileUrl: String = "",
    val roomList: PersistentList<RoomInfo> = persistentListOf(),
    val isLocationPermissionDialogVisible: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false
) {
    companion object {
        val dummyDataState = MainUiState(
            name = "조익성",
            email = "ikseung@ikseong.com",
            address = "서울특별시 광진구",
            profileUrl = "",
            roomList = persistentListOf(
                RoomInfo(
                    roomId = 1,
                    roomName = "조익성의 방",
                    hostName = "조익성",
                    roomLink = "https://www.naver.com",
                    isHost = true
                ),
                RoomInfo(
                    roomId = 2,
                    roomName = "친구의 방",
                    hostName = "친구",
                    roomLink = "https://www.google.com",
                    isHost = false
                ),
                RoomInfo(
                    roomId = 3,
                    roomName = "가족의 방",
                    hostName = "가족",
                    roomLink = "https://www.daum.net",
                    isHost = false
                ),
                RoomInfo(
                    roomId = 4,
                    roomName = "동료의 방",
                    hostName = "동료",
                    roomLink = "https://www.youtube.com",
                    isHost = false
                )
            ),
            isLocationPermissionDialogVisible = false,
            isEditProfileDialogVisible = false
        )
    }
}

data class RoomInfo(
    val roomId: Long = 0L,
    val roomName: String = "",
    val hostName: String = "",
    val roomLink: String = "",
    val isHost: Boolean = false
)