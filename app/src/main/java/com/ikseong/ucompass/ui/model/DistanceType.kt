package com.ikseong.ucompass.ui.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ikseong.ucompass.R

enum class DistanceType(
    val minDistance: Int,
    val size: Dp,
    @DrawableRes val iconRes: Int
) {
    TWO_THOUSAND(2000, 24.dp, R.drawable.ic_participant_pin_6),
    ONE_THOUSAND_FIVE_HUNDRED(1500, 38.dp, R.drawable.ic_participant_pin_5),
    ONE_THOUSAND(1000, 52.dp, R.drawable.ic_participant_pin_4),
    FIVE_HUNDRED(500, 66.dp, R.drawable.ic_participant_pin_3),
    TWO_HUNDRED(200, 74.dp, R.drawable.ic_participant_pin_2),
    ZERO(0, 82.dp, R.drawable.ic_participant_pin_1);

    companion object {
        fun fromDistance(distance: Int): DistanceType {
            return entries.firstOrNull { distance >= it.minDistance } ?: ZERO
        }
    }
}