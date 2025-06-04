package com.ikseong.ucompass.ui.util

import android.content.Context
import android.util.Log
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.util.viewutil.ToastUtil.showToast
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

/**
 * 위치 관련 유틸리티 함수들
 */
object LocationUtil {
    private const val TAG = "LocationUtil"
    private const val LOCATION_UPDATE_INTERVAL = 10000L // 10초
    
    /**
     * 위치 측정 방식을 나타내는 열거형
     */
    enum class LocationSource {
        GPS, RTT, ANY
    }
    
    /**
     * 현재 위치를 1회성으로 가져옵니다.
     * 
     * @param context 컨텍스트
     * @param source 위치 측정 방식 (GPS, RTT, ANY)
     * @param timeoutMs 타임아웃 시간 (밀리초), 기본값 10초
     * @param showToast 토스트 메시지 표시 여부, 기본값 false
     * @return 현재 위치 (LatLng)
     * @throws TimeoutCancellationException 타임아웃 발생 시
     * @throws Exception 위치를 가져오지 못한 경우
     */
    suspend fun getCurrentLocation(
        context: Context,
        source: LocationSource = LocationSource.ANY,
        timeoutMs: Long = 10000,
        showToast: Boolean = false
    ): LatLng = withContext(Dispatchers.IO) {
        try {
            withTimeout(timeoutMs.milliseconds) {
                when (source) {
                    LocationSource.GPS -> getGpsLocation(context, showToast)
                    LocationSource.RTT -> getRttLocation(context, showToast)
                    LocationSource.ANY -> getAnyLocation(context, showToast)
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "위치 가져오기 타임아웃: $e")
            throw e
        } catch (e: CancellationException) {
            Log.e(TAG, "위치 가져오기 취소됨: $e")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "위치 가져오기 실패: $e")
            throw Exception("위치를 가져오지 못했습니다: ${e.message}", e)
        }
    }
    
    /**
     * GPS를 사용하여 현재 위치를 1회성으로 가져옵니다.
     */
    private suspend fun getGpsLocation(context: Context, showToast: Boolean): LatLng {
        return suspendCancellableCoroutine { continuation ->
            GpsLocationUtil.getLocationOnce(context) { location ->
                if (location != null) {
                    if (showToast) {
                        val accuracy = GpsLocationUtil.getLastLocationAccuracy()
                        val message = "GPS 위치 측정 완료\n" +
                                "- 정확도: ${accuracy}m\n" +
                                "- 위치: ${location.latitude.format(5)}, ${location.longitude.format(5)}"
                        showToast(context, message)
                    }
                    
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                } else if (continuation.isActive) {
                    continuation.resume(LatLng(0.0, 0.0)) // 기본값
                }
            }
            
            continuation.invokeOnCancellation {
                // 취소되면 리소스 정리
                GpsLocationUtil.stopLocationUpdates(context)
            }
        }
    }
    
    /**
     * WiFi RTT를 사용하여 현재 위치를 1회성으로 가져옵니다.
     */
    private suspend fun getRttLocation(context: Context, showToast: Boolean): LatLng {
        return suspendCancellableCoroutine { continuation ->
            WifiRttUtil.getRttLocationOnce(context) { location ->
                if (location != null) {
                    if (showToast) {
                        val rttAccessPoints = WifiRttUtil.getLastRttAccessPointCount()
                        val message = "WiFi RTT 위치 측정 완료\n" +
                                "- 측정된 AP 개수: $rttAccessPoints\n" +
                                "- 위치: ${location.latitude.format(5)}, ${location.longitude.format(5)}"
                        showToast(context, message)
                    }
                    
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                } else if (continuation.isActive) {
                    // RTT가 실패하면 GPS로 시도
                    GpsLocationUtil.getLocationOnce(context) { gpsLocation ->
                        if (gpsLocation != null) {
                            if (showToast) {
                                val accuracy = GpsLocationUtil.getLastLocationAccuracy()
                                val message = "WiFi RTT 실패, GPS 위치 측정 완료\n" +
                                        "- 정확도: ${accuracy}m\n" +
                                        "- 위치: ${gpsLocation.latitude.format(5)}, ${gpsLocation.longitude.format(5)}"
                                showToast(context, message)
                            }
                            
                            if (continuation.isActive) {
                                continuation.resume(gpsLocation)
                            }
                        } else if (continuation.isActive) {
                            continuation.resume(LatLng(0.0, 0.0)) // 기본값
                        }
                    }
                }
            }
            
            continuation.invokeOnCancellation {
                // 취소되면 리소스 정리
                WifiRttUtil.stopRttUpdateTimer()
                GpsLocationUtil.stopLocationUpdates(context)
            }
        }
    }
    
    /**
     * 가능한 방법(WiFi RTT 또는 GPS)을 사용하여 현재 위치를 1회성으로 가져옵니다.
     * 먼저 RTT를 시도하고 실패하면 GPS를 사용합니다.
     */
    private suspend fun getAnyLocation(context: Context, showToast: Boolean): LatLng {
        // 먼저 RTT를 시도하고 실패하면 GPS 사용
        return try {
            if (WifiRttUtil.isWifiRttSupported(context)) {
                getRttLocation(context, showToast)
            } else {
                getGpsLocation(context, showToast)
            }
        } catch (e: Exception) {
            getGpsLocation(context, showToast)
        }
    }

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

    fun offsetLatLng(
        origin: LatLng,
        distanceMeter: Double,
        angleDegrees: Double
    ): LatLng {
        val R = 6378137.0 // Earth radius (m)
        val bearingRad = Math.toRadians(angleDegrees.toDouble())
        val lat1 = Math.toRadians(origin.latitude)
        val lon1 = Math.toRadians(origin.longitude)

        val lat2 = asin(
            sin(lat1) * cos(distanceMeter / R) +
                    cos(lat1) * sin(distanceMeter / R) * cos(bearingRad)
        )

        val lon2 = lon1 + atan2(
            sin(bearingRad) * sin(distanceMeter / R) * cos(lat1),
            cos(distanceMeter / R) - sin(lat1) * sin(lat2)
        )

        return LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }

}