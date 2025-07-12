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
     * GPS 위치를 1회성으로 가져옵니다.
     * 
     * @param context 컨텍스트
     * @param onLocationResult 위치 결과 콜백
     */
    @SuppressLint("MissingPermission")
    fun getLocationOnce(context: Context, onLocationResult: (LatLng?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // 기존 콜백이 있다면 제거
//        stopLocationUpdates(context)
        
        // 먼저 마지막 알려진 위치 요청
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    lastLocationAccuracy = location.accuracy
                    onLocationResult(latLng)
                } else {
                    // 마지막 위치를 사용할 수 없는 경우 새 위치 요청
                    requestNewLocation(context, onLocationResult)
                }
            }
            .addOnFailureListener {
                // 오류 발생 시 새 위치 요청
                requestNewLocation(context, onLocationResult)
            }
    }
    
    /**
     * 새 위치를 1회성으로 요청합니다.
     */
    @SuppressLint("MissingPermission")
    private fun requestNewLocation(context: Context, onLocationResult: (LatLng?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // 위치 요청 설정 (고정밀도, 최대 5초 타임아웃)
        val locationRequest = LocationRequest.Builder(5000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdates(1) // 1회만 업데이트
            .build()
        
        // 1회성 콜백 생성
        val singleLocationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                // 콜백 제거 (더 이상 업데이트를 받지 않음)
                fusedLocationClient.removeLocationUpdates(this)
                
                result.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    lastLocationAccuracy = location.accuracy
                    onLocationResult(latLng)
                } ?: run {
                    onLocationResult(null)
                }
            }
        }
        
        try {
            // 위치 업데이트 요청
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                singleLocationCallback,
                Looper.getMainLooper()
            )
            
            // 안전을 위해 5초 후에 콜백 제거 (타임아웃)
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                fusedLocationClient.removeLocationUpdates(singleLocationCallback)
                onLocationResult(null) // 타임아웃 시 null 반환
            }, 5000)
            
        } catch (e: Exception) {
            Log.e(TAG, "위치 요청 오류: ${e.message}")
            onLocationResult(null)
        }
    }

    /**
     * GPS 위치 업데이트 시작
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        context: Context,
        intervalMs: Long = 10000, // 10초로 변경 (배터리 효율성 향상)
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
                    Log.d("RoomRoute", "위치 업데이트: ${latLng.latitude}, ${latLng.longitude}, 정확도: $lastLocationAccuracy")
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