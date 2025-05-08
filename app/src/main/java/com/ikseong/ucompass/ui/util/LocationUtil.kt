package com.ikseong.ucompass.ui.util

import com.ikseong.ucompass.ui.model.Direction
import com.naver.maps.geometry.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.roundToInt

/**
 * 위치 관련 유틸리티 함수들
 */
object LocationUtil {
    
    /**
     * 두 지점 간의 방향(Direction)을 계산합니다.
     *
     * @param from 시작 위치 (내 위치)
     * @param to 목표 위치 (참가자 위치)
     * @return 방향 (Direction Enum)
     */
    fun calculateDirection(from: LatLng, to: LatLng): Direction {
        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lon2 = Math.toRadians(to.longitude)
        
        val dLon = lon2 - lon1
        
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        
        var bearing = Math.toDegrees(atan2(y, x))
        if (bearing < 0) {
            bearing += 360
        }
        
        // 방향을 Direction 열거형으로 변환
        return when (bearing) {
            in 0.0..22.5, in 337.5..360.0 -> Direction.N
            in 22.5..67.5 -> Direction.NE
            in 67.5..112.5 -> Direction.E
            in 112.5..157.5 -> Direction.SE
            in 157.5..202.5 -> Direction.S
            in 202.5..247.5 -> Direction.SW
            in 247.5..292.5 -> Direction.W
            in 292.5..337.5 -> Direction.NW
            else -> Direction.N // 기본값
        }
    }
    
    /**
     * 두 지점 간의 거리(미터)를 계산합니다.
     *
     * @param from 시작 위치 (내 위치)
     * @param to 목표 위치 (참가자 위치)
     * @return 거리 (미터)
     */
    fun calculateDistance(from: LatLng, to: LatLng): Double {
        val earthRadius = 6371000.0 // 지구 반지름 (미터)
        
        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lon2 = Math.toRadians(to.longitude)
        
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * 거리를 계산하고 정수로 변환합니다.
     */
    fun calculateDistanceInMeters(from: LatLng, to: LatLng): Int {
        return calculateDistance(from, to).roundToInt()
    }
} 