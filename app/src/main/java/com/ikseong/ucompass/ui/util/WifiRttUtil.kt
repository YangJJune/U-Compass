package com.ikseong.ucompass.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.naver.maps.geometry.LatLng
import java.util.Timer
import java.util.TimerTask

/**
 * WiFi RTT 관련 기능을 제공하는 유틸리티 클래스
 */
object WifiRttUtil {
    private const val TAG = "WifiRttUtil"
    private var rttCheckTimer: Timer? = null
    
    /**
     * WiFi RTT 지원 여부 확인
     */
    fun isWifiRttSupported(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val wifiRttManager = context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as? WifiRttManager
            return wifiRttManager != null && wifiRttManager.isAvailable
        }
        return false
    }
    
    /**
     * WiFi RTT 측정을 정기적으로 수행하는 타이머 시작
     */
    fun startRttUpdateTimer(
        context: Context,
        intervalMs: Long = 10000, // 기본 10초
        onLocationUpdate: (LatLng?) -> Unit
    ) {
        stopRttUpdateTimer() // 기존 타이머 중지
        
        rttCheckTimer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (isWifiRttSupported(context)) {
                        Log.d(TAG, "WiFi RTT 지원됨, RTT 위치 측정 시도")
                        performRttRanging(context) { rttLocation ->
                            onLocationUpdate(rttLocation)
                        }
                    } else {
                        Log.d(TAG, "WiFi RTT 지원되지 않음")
                        onLocationUpdate(null)
                    }
                }
            }, 0, intervalMs)
        }
    }
    
    /**
     * WiFi RTT 측정 타이머 중지
     */
    fun stopRttUpdateTimer() {
        rttCheckTimer?.cancel()
        rttCheckTimer = null
    }
    
    /**
     * WiFi RTT 측정 수행
     */
    @SuppressLint("MissingPermission")
    private fun performRttRanging(
        context: Context,
        onRttLocation: (LatLng?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            onRttLocation(null)
            return
        }
        
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiRttManager = context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as? WifiRttManager
            
            if (wifiRttManager == null) {
                onRttLocation(null)
                return
            }
            
            // WiFi 스캔 시작
            wifiManager.startScan()
            
            // 스캔 결과 가져오기
            val scanResults = wifiManager.scanResults.filter { 
                // RTT 지원 AP만 필터링
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    it.is80211mcResponder
                } else {
                    false
                }
            }
            
            if (scanResults.isEmpty()) {
                Log.d(TAG, "RTT를 지원하는 AP가 없습니다")
                onRttLocation(null)
                return
            }
            
            // RTT 요청 준비
            val rangingRequest = RangingRequest.Builder().apply {
                // 최대 허용된 AP 수 확인
                val maxRttPeers = RangingRequest.getMaxPeers()
                val apList = scanResults.take(maxRttPeers).toList()
                addAccessPoints(apList)
            }.build()
            
            // RTT 측정 시작
            wifiRttManager.startRanging(
                rangingRequest,
                context.mainExecutor,
                object : RangingResultCallback() {
                    override fun onRangingResults(results: List<RangingResult>) {
                        // RTT 결과로부터 위치 계산
                        val rttLocation = calculatePositionFromRttScan(results, scanResults)
                        
                        if (rttLocation != null) {
                            Log.d(TAG, "RTT 위치 계산 성공: $rttLocation")
                            // 메인 스레드에서 콜백 호출
                            Handler(Looper.getMainLooper()).post {
                                onRttLocation(rttLocation)
                            }
                        } else {
                            Log.d(TAG, "RTT 위치 계산 실패")
                            onRttLocation(null)
                        }
                    }
                    
                    override fun onRangingFailure(code: Int) {
                        Log.e(TAG, "RTT ranging failed: $code")
                        onRttLocation(null)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "WiFi RTT ranging error: ${e.message}")
            onRttLocation(null)
        }
    }
    
    /**
     * RTT 측정 결과와 스캔 결과를 사용하여 위치 계산
     * AP 위치 정보는 알 수 없으므로, 스캔된 AP의 신호 세기와 RTT 거리를 조합하여
     * 상대적인 위치를 추정
     */
    private fun calculatePositionFromRttScan(
        rangingResults: List<RangingResult>, 
        scanResults: List<ScanResult>
    ): LatLng? {
        if (rangingResults.size < 3) {
            Log.d(TAG, "삼각측량을 위해선 최소 3개의 AP가 필요합니다. 현재 ${rangingResults.size}개")
            return null
        }
        
        try {
            // 먼저 RSSI(신호 세기) 정보로부터 대략적인 AP 위치 추정
            // 이 방법은 정확하지는 않지만 RTT 거리와 함께 사용하면 상대적인 위치 추정 가능
            val apEstimatedPositions = mutableMapOf<String, Triple<Double, Double, Int>>()
            
            // 각 AP에 대한 정보 수집
            for (scan in scanResults) {
                val macAddress = scan.BSSID
                val rssi = scan.level // 신호 세기 (dBm)
                
                // RSSI를 거리로 변환 (단순화된 계산식)
                val estimatedDistance = calculateDistanceFromRssi(rssi)
                
                // 임시 위치 할당 (각 AP를 중심으로 가상의 좌표계 설정)
                // 이 좌표계는 실제 지리적 위치가 아닌 상대적 위치
                val angle = Math.random() * 2 * Math.PI // 임의의 각도
                val x = estimatedDistance * Math.cos(angle)
                val y = estimatedDistance * Math.sin(angle)
                
                apEstimatedPositions[macAddress] = Triple(x, y, rssi)
            }
            
            // RTT 측정 결과와 매칭하여 위치 조정
            val validResults = rangingResults.filter { result ->
                result.status == RangingResult.STATUS_SUCCESS
            }
            
            if (validResults.isEmpty()) {
                Log.d(TAG, "유효한 RTT 측정 결과가 없습니다.")
                return null
            }
            
            // 가장 강한 신호의 AP를 기준점으로 설정
            val referenceAp = validResults.maxByOrNull { result ->
                apEstimatedPositions[result.mac.toString()]?.third ?: Int.MIN_VALUE
            }
            
            if (referenceAp == null) {
                Log.d(TAG, "기준점으로 사용할 AP를 찾을 수 없습니다.")
                return null
            }
            
            // 기준 AP 정보
            val refMac = referenceAp.mac.toString()
            val refPosition = apEstimatedPositions[refMac] ?: return null
            val refDistance = referenceAp.distanceMm / 1000.0 // 미터 단위로 변환
            
            // 사용자 위치 추정 (단순화된 방법)
            var sumX = 0.0
            var sumY = 0.0
            var weightSum = 0.0
            
            for (result in validResults) {
                val mac = result.mac.toString()
                val apPosition = apEstimatedPositions[mac] ?: continue
                
                val distance = result.distanceMm / 1000.0 // 미터로 변환
                val weight = 1.0 / (distance * distance) // 거리의 역수를 가중치로 사용
                
                // 기준점으로부터의 상대적 위치 계산
                val dx = apPosition.first - refPosition.first
                val dy = apPosition.second - refPosition.second
                
                // 가중 평균 계산
                sumX += dx * weight
                sumY += dy * weight
                weightSum += weight
            }
            
            if (weightSum > 0) {
                val x = sumX / weightSum
                val y = sumY / weightSum
                
                // 임의의 기준 좌표 (서울 시청)
                val baseLatLng = LatLng(37.5662, 126.9785)
                
                // 상대적 위치를 실제 좌표로 변환 (단순 근사)
                // 1도는 약 111km이므로 미터를 도 단위로 변환
                val latOffset = y / 111000.0
                val lngOffset = x / (111000.0 * Math.cos(Math.toRadians(baseLatLng.latitude)))
                
                return LatLng(
                    baseLatLng.latitude + latOffset,
                    baseLatLng.longitude + lngOffset
                )
            }
            
            return null
        } catch (e: Exception) {
            Log.e(TAG, "위치 계산 중 오류 발생: ${e.message}")
            return null
        }
    }
    
    /**
     * RSSI를 대략적인 거리로 변환 (단순화된 공식)
     * 실제 환경에서는 더 복잡한 모델이 필요할 수 있음
     */
    private fun calculateDistanceFromRssi(rssi: Int): Double {
        val txPower = -59 // 기준 전송 파워 (1m 거리에서의 RSSI, 일반적으로 -59dBm)
        
        return if (rssi == 0) {
            -1.0 // 신호 없음
        } else {
            Math.pow(10.0, (txPower - rssi) / 20.0)
        }
    }
} 