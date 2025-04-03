package com.example.composesensor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun ArrowDirectionIndicator(bearingToTarget: Float) {
    // 현재 기기 방위각을 센서로부터 얻음 (항상 0~360도 값 유지)
    val azimuth = rememberAzimuth()
    // 목표 방향 대비 기기 방향의 차이각 계산
    val rotationAngle = (bearingToTarget - azimuth + 360f) % 360f

    Image(
        imageVector = Icons.Filled.PlayArrow,  // 화살표 아이콘 (기본이 위쪽을 가리키는 그림)
        contentDescription = "목표 방향 화살표",
        modifier = Modifier
            .size(100.dp)
            .rotate(rotationAngle)
    )
}
