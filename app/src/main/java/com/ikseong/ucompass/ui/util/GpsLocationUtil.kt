package com.ikseong.ucompass.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng

/**
 * GPS 위치 측정 관련 기능을 제공하는 유틸리티 클래스
 */
object GpsLocationUtil {
    private const val TAG = "GpsLocationUtil"
    private var locationCallback: LocationCallback? = null
    private var lastLocationAccuracy: Float = 0f
    
    /**
     * 마지막으로 측정된 위치의 정확도를 반환합니다.
     * @return 정확도(미터)
     */
    fun getLastLocationAccuracy(): Float {
        return lastLocationAccuracy
    }
    
    /**
     * GPS 위치 업데이트 시작
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        context: Context,
        intervalMs: Long = 10000, // 기본 10초
        onLocationUpdate: (LatLng) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // 이전 콜백이 있다면 제거
        stopLocationUpdates(context)
        
        // 위치 요청 설정
        val locationRequest = LocationRequest.Builder(intervalMs)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        
        // 새 콜백 생성
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    lastLocationAccuracy = location.accuracy
                    onLocationUpdate(latLng)
                }
            }
        }
        
        try {
            // 위치 업데이트 요청
            locationCallback?.let {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    it,
                    Looper.getMainLooper()
                )
                
                // 즉시 한 번 위치 요청
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(location.latitude, location.longitude)
                        lastLocationAccuracy = location.accuracy
                        onLocationUpdate(latLng)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "위치 업데이트 오류: ${e.message}")
        }
    }
    
    /**
     * GPS 위치 업데이트 중지
     */
    fun stopLocationUpdates(context: Context) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(it)
            }
            locationCallback = null
        } catch (e: Exception) {
            Log.e(TAG, "위치 업데이트 중지 오류: ${e.message}")
        }
    }
} 