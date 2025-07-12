package com.ikseong.ucompass.ui.main.viewmodel

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class RoomInfo(
    val roomId: Long,
    val roomTitle: String,
    val roomDescription: String,
    val participantCount: Int,
    val participantList: List<String>,
    val isHost: Boolean,
)

data class MainUiState(
    val name: String = "",
    val email: String = "",
    val address: String = "위치를 불러오는 중입니다.",
    val roomList: PersistentList<RoomInfo> = persistentListOf(),
    val isLocationPermissionDialogVisible: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false,
    val isLoadingRoomList: Boolean = false,
    val isLoadingLocation: Boolean = false,
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