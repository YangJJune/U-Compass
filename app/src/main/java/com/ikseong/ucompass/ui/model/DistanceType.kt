package com.ikseong.ucompass.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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