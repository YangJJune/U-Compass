package com.ikseong.ucompass.ui.room.viewmodel

import com.ikseong.ucompass.ui.common.component.MapMarker
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.model.DistanceType
import com.ikseong.ucompass.ui.model.ParticipantInfo

data class RoomUiState(
    val address: String = "주소를 불러오는 중입니다.",
    val roomId: Long = 1L,
    val roomName: String = "",
    val participantInfo: List<ParticipantInfo> = listOf(),
    val participantCount: Int = 0,
    val participantState: List<ParticipantState> = listOf(),
    val mapMarkers: List<MapMarker> = listOf(),
    val isHost: Boolean = false,
    val isSearchMode: Boolean = false,
    val isMapVisible: Boolean = false,
    val isRoomDeleteDialogVisible: Boolean = false,
    val contentWidthPx: Int = 0,
    val contentHeightPx: Int = 0,
    val isLoadingRoomInfo: Boolean = false,
    val isConnectingSocket: Boolean = false,
)

data class ParticipantState(
    val name: String = "",
    val profileUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var direction: Direction? = null,
    var distance: Int? = null,
    val isShown: Boolean = true,
) {
    companion object {
        fun ParticipantState.getDistanceType(): DistanceType =
            when (this.distance) {
                in 0..200 -> DistanceType.ZERO
                in 201..500 -> DistanceType.TWO_HUNDRED
                in 501..1000 -> DistanceType.FIVE_HUNDRED
                in 1001..1500 -> DistanceType.ONE_THOUSAND
                in 1501..2000 -> DistanceType.ONE_THOUSAND_FIVE_HUNDRED
                else -> DistanceType.TWO_THOUSAND
            }
    }
}