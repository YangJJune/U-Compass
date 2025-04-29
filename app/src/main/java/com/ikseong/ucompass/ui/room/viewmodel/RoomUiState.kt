package com.ikseong.ucompass.ui.room.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RoomUiState(
    val address: String = "",
    val roomId: String = "",
    val roomName: String = "",
    val participantsNumber: Int = 0,
    val participantInfo: List<ParticipantInfo> = listOf()
)

data class ParticipantInfo(
    val name: String = "",
    val profileUrl: String = "",
    val direction: String = "",
    val distance: Int = 0,
    val isShown: Boolean = false,
) {
    fun getDistanceType(): DistanceType =
        when (this.distance) {
            in 0..200 -> DistanceType.ZERO
            in 201..500 -> DistanceType.TWO_HUNDRED
            in 501..1000 -> DistanceType.FIVE_HUNDRED
            in 1001..1500 -> DistanceType.ONE_THOUSAND
            in 1501..2000 -> DistanceType.ONE_THOUSAND_FIVE_HUNDRED
            else -> DistanceType.TWO_THOUSAND
        }
}

enum class DistanceType(
    val minDistance: Int,
    val size: Dp,
    val color: Color
) {
    TWO_THOUSAND(2000, 24.dp, Color(0xFFCCF9EA)),
    ONE_THOUSAND_FIVE_HUNDRED(1500, 38.dp, Color(0xFFA0F5E1)),
    ONE_THOUSAND(1000, 52.dp, Color(0xFF7FEDC6)),
    FIVE_HUNDRED(500, 66.dp, Color(0xFF4FEAB3)),
    TWO_HUNDRED(200, 74.dp, Color(0xFF26E6A1)),
    ZERO(0, 82.dp, Color(0xFF00E397))
}
