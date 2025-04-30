package com.ikseong.ucompass.ui.model

data class ParticipantInfo(
    val name: String = "",
    val profileUrl: String = "",
    val direction: Direction = Direction.NE,
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