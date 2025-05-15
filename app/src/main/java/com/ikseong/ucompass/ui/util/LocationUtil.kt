package com.ikseong.ucompass.ui.util

import android.content.Context
import android.util.Log
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.util.viewutil.ToastUtil.showToast
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
    private const val TAG = "RoomScreen"
    private const val LOCATION_UPDATE_INTERVAL = 10000L // 10초
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
    private fun calculateDistance(from: LatLng, to: LatLng): Double {
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

    // 위치 업데이트 시작 - WiFi RTT와 GPS 모두 시작
    private fun startLocationUpdates(context: Context, onLocationUpdate: (LatLng) -> Unit) {
        // 마지막으로 사용된 위치 소스를 추적
        var lastUsedSource = "초기화"

        // WiFi RTT 시작 (10초마다 체크하고, 지원될 때만 위치 계산)
        WifiRttUtil.startRttUpdateTimer(context, LOCATION_UPDATE_INTERVAL) { rttLocation ->
            // RTT로 위치를 얻을 수 있다면 사용, 아니면 무시
            rttLocation?.let {
                Log.d(TAG, "WiFi RTT로 측정된 위치 사용: $it")

                // 위치 소스가 변경되었거나 처음 사용되는 경우에만 토스트 표시
                if (lastUsedSource != "WiFi RTT") {
                    lastUsedSource = "WiFi RTT"

                    // WiFi RTT 사용 시 토스트 메시지 표시
                    val rttAccessPoints = WifiRttUtil.getLastRttAccessPointCount()
                    val message = "WiFi RTT 위치 측정 중\n" +
                            "- 측정된 AP 개수: $rttAccessPoints\n" +
                            "- 위치: ${it.latitude.format(5)}, ${it.longitude.format(5)}"

                    showToast(context, message)
                }

                onLocationUpdate(it)
            }
        }

        // GPS 위치 측정 시작 (기본 위치 소스로 사용)
        GpsLocationUtil.startLocationUpdates(context, LOCATION_UPDATE_INTERVAL) { gpsLocation ->
            Log.d(TAG, "GPS 위치 사용: $gpsLocation")

            // 위치 소스가 변경되었거나 처음 사용되는 경우에만 토스트 표시
            if (lastUsedSource != "GPS") {
                lastUsedSource = "GPS"

                // GPS 사용 시 토스트 메시지 표시
                val accuracy = GpsLocationUtil.getLastLocationAccuracy()
                val message = "GPS 위치 측정 중\n" +
                        "- 정확도: ${accuracy}m\n" +
                        "- 위치: ${gpsLocation.latitude.format(5)}, ${gpsLocation.longitude.format(5)}"

                showToast(context, message)
            }

            onLocationUpdate(gpsLocation)
        }
    }

    // 위치 업데이트 중지 - 모든 소스 중지
    private fun stopLocationUpdates(context: Context) {
        WifiRttUtil.stopRttUpdateTimer()
        GpsLocationUtil.stopLocationUpdates(context)
    }


    // Double 값을 지정된 소수점 자릿수로 포맷팅하는 확장 함수
    private fun Double.format(digits: Int): String = String.format("%.${digits}f", this)

} 