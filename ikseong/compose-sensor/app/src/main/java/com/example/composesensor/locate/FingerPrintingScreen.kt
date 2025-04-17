package com.example.composesensor.locate

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun FingerprintingRoute(
    padding: PaddingValues
) {
    FingerprintingScreen(padding = padding)
}

@Composable
fun FingerprintingScreen(
    padding: PaddingValues
) {
    val context = LocalContext.current
    var currentPosition by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var status by remember { mutableStateOf("데이터 수집 대기 중") }

    // 예제에서는 LaunchedEffect를 이용해 2초마다 Wi‑Fi 스캔을 수행
    LaunchedEffect(Unit) {
        while (true) {
            // scanWifi 함수는 Wi‑Fi 스캔 결과를 ScanResult 목록으로 반환합니다.
            scanWifi(context) { scanResults ->
                val candidate = improvedMatchFingerprint(scanResults)
                if (candidate != null) {
                    currentPosition = Pair(candidate.lat, candidate.lon)
                    status = "위치 추정 성공"
                    Log.d("FingerprintingRoute", "위치 추정 성공: $currentPosition")
                } else {
                    currentPosition = null
                    status = "위치 추정 실패"
                    Log.d("FingerprintingRoute", "위치 추정 실패")
                }
            }
            delay(2000L)
        }
    }

    // 로컬 좌표를 기반으로 GPS 좌표(위도/경도)로 변환
//    val gpsCoordinates = estimatedLocalPosition?.let { convertLocalToGPS(it, anchor) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 카드: 로컬 좌표(미터 단위)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "실내 위치 정보 (로컬 좌표)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
//                if (estimatedLocalPosition != null) {
//                    LocationInfoItem("X (미터)", "${estimatedLocalPosition!!.first}")
//                    LocationInfoItem("Y (미터)", "${estimatedLocalPosition!!.second}")
//                } else {
//                    Text(text = "위치 정보를 가져오는 중...", fontSize = 16.sp)
//                }
            }
        }


        // 네이버 지도 컴포넌트 (MyNaverMap은 따로 구현되어 있다고 가정)
        MyNaverMap(
            modifier = Modifier.padding(top = 16.dp),
            latLng = currentPosition?.let {
                LatLng(it.first, it.second)
            }
        )


    }
}

// 각 기준 지점에 대한 Fingerprint 데이터 모델 (위도/경도 사용)
data class ReferenceFingerprint(
    val lat: Double,    // 위도
    val lon: Double,    // 경도
    // 각 AP의 BSSID와 해당 기준 지점에서 측정한 평균 RSSI 값
    val signals: Map<String, Int>
)

// 예제로 사용할 사전 데이터 (실제 프로젝트에서는 다양한 위치에서 충분한 데이터를 수집해야 함)
val referenceFingerprints = listOf(
    ReferenceFingerprint(
        lat = 37.5665,   // 예: 서울 시청 근처
        lon = 126.9780,
        signals = mapOf(
            "00:11:22:33:44:55" to -45,
            "66:77:88:99:AA:BB" to -50,
            "CC:DD:EE:FF:00:11" to -55
        )
    ),
    ReferenceFingerprint(
        lat = 35.1796,   // 예: 부산 중심부
        lon = 129.0756,
        signals = mapOf(
            "00:11:22:33:44:55" to -60,
            "66:77:88:99:AA:BB" to -40,
            "CC:DD:EE:FF:00:11" to -65
        )
    )
    // 여기에 추가적인 데이터 수집 결과를 계속 추가
)


fun matchFingerprintImproved(
    scanResults: List<android.net.wifi.ScanResult>,
    k: Int = 3
): Pair<Double, Double>? {
    // 현재 스캔 결과를 Map으로 구성 (대/소문자 통일)
    val measuredFingerprint = mutableMapOf<String, Int>()
    for (result in scanResults) {
        measuredFingerprint[result.BSSID.uppercase()] = result.level
    }

    // 사전 데이터(referenceFingerprint)와의 거리를 계산
    val fingerprintDistances = referenceFingerprints.map { ref ->
        // 각 기준 지점에서, 해당 AP가 있으면 신호 차이, 없으면 큰 패널티 값을 적용
        val squaredDiffSum = ref.signals.entries.sumOf { entry ->
            val bssid = entry.key.uppercase()
            val refRssi = entry.value.toDouble()
            val measured = measuredFingerprint[bssid] ?: 100 // AP가 누락된 경우, RSSI에 큰 패널티 (예: 100)
            val diff = measured - refRssi
            diff * diff
        }
        val distance = sqrt(squaredDiffSum)
        Pair(ref, distance)
    }

    // 거리가 가까운 순으로 정렬
    val sortedResults = fingerprintDistances.sortedBy { it.second }

    // 상위 k개의 기준 데이터로 가중 평균 계산 (가중치 = 1/(거리+epsilon))
    val epsilon = 0.1 // 0으로 나누는 것을 피하기 위한 작은 값
    var weightedLatSum = 0.0
    var weightedLonSum = 0.0
    var totalWeight = 0.0

    for (i in 0 until min(k, sortedResults.size)) {
        val (ref, distance) = sortedResults[i]
        val weight = 1.0 / (distance + epsilon)
        weightedLatSum += ref.lat * weight
        weightedLonSum += ref.lon * weight
        totalWeight += weight
    }

    return if (totalWeight > 0) {
        Pair(weightedLatSum / totalWeight, weightedLonSum / totalWeight)
    } else null
}

private fun scanWifi(
    context: Context,
    onResult: (List<android.net.wifi.ScanResult>) -> Unit
) {
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
    val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    val receiver = object : BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        override fun onReceive(ctx: Context?, intent: Intent?) {
            ctx?.unregisterReceiver(this)
            val results = if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            } else wifiManager.scanResults
            onResult(results)
        }
    }
    context.registerReceiver(receiver, intentFilter)
    wifiManager.startScan()
}

/**
 * 현재 스캔 결과(ScanResult 목록)와 사전 Fingerprint 데이터를 비교하여
 * 가장 유사한 기준 Fingerprint(ReferenceFingerprint)를 반환합니다.
 * 만약 비교할 데이터가 없으면 null 반환.
 */
fun matchFingerprintLatLon(scanResults: List<android.net.wifi.ScanResult>): ReferenceFingerprint? {
    // 현재 스캔 결과에서 BSSID와 RSSI를 Map 형태로 구성
    val measuredFingerprint = mutableMapOf<String, Int>()
    for (result in scanResults) {
        measuredFingerprint[result.BSSID] = result.level
    }

    var bestCandidate: ReferenceFingerprint? = null
    var minDistance = Double.MAX_VALUE

    // 각 기준 지점의 Fingerprint 데이터와 측정 데이터 간의 유클리드 거리 계산
    for (ref in referenceFingerprints) {
        var sumOfSquaredDiffs = 0.0

        // 기준 Fingerprint에 있는 각 AP 신호에 대해
        for ((bssid, refRssi) in ref.signals) {
            val measuredRssi = measuredFingerprint[bssid]
            if (measuredRssi != null) {
                val diff = measuredRssi - refRssi
                sumOfSquaredDiffs += diff * diff.toDouble()
            } else {
                // 해당 AP가 스캔되지 않았다면 패널티 값 적용 (예, 100)
                sumOfSquaredDiffs += 100.0
            }
        }
        val distance = kotlin.math.sqrt(sumOfSquaredDiffs)
        if (distance < minDistance) {
            minDistance = distance
            bestCandidate = ref
        }
    }
    return bestCandidate
}

/**
 * 현재 스캔 결과(ScanResult 목록)와 사전 Fingerprint 데이터를 비교하여
 * 가장 유사한 기준 Fingerprint(ReferenceFingerprint)를 반환합니다.
 * 개선 사항:
 *  - AP별 차이를 정규화하여 비교
 *  - 누락(AP 미검출)에 대해 패널티를 부여하되, 너무 큰 값 대신 상수로 보정
 *  - 전체 차이가 너무 큰 경우(신뢰도 낮은 경우) null 반환으로 매칭 실패 처리
 */
fun improvedMatchFingerprint(scanResults: List<android.net.wifi.ScanResult>): ReferenceFingerprint? {
    // 현재 스캔 결과를 Map으로 구성 (BSSID -> RSSI)
    val measuredFingerprint = mutableMapOf<String, Int>()
    for (result in scanResults) {
        measuredFingerprint[result.BSSID] = result.level
    }

    var bestCandidate: ReferenceFingerprint? = null
    var minNormalizedDistance = Double.MAX_VALUE

    // 모든 참조 데이터에 대해 정규화된 차이 계산
    for (ref in referenceFingerprints) {
        // AP가 존재하는 경우에만 계산 (누락된 항목에는 고정 패널티를 부여)
        var sumNormalizedDiff = 0.0
        var count = 0

        for ((bssid, refRssi) in ref.signals) {
            val measuredRssi = measuredFingerprint[bssid]
            if (measuredRssi != null) {
                // 두 RSSI 값의 차이를 절대치로 계산하고, 흔히 RSSI 값 범위(-100 ~ 0)를 고려해 정규화
                // (예를 들어, 0 ~ 100 범위로 만들기 위해, 차이에 100을 더함)
                val diff = abs(measuredRssi - refRssi).toDouble()
                val normalizedDiff = diff / 100.0  // 100이면 최대 차이로 간주
                sumNormalizedDiff += normalizedDiff
                count++
            } else {
                // 만약 해당 AP가 측정되지 않았다면, 패널티 0.5를 부여 (상황에 따라 조정)
                sumNormalizedDiff += 0.5
                count++
            }
        }

        // 평균 정규화 오차 계산
        val avgNormalizedDiff = sumNormalizedDiff / count.toDouble()

        // 만약 오차가 너무 크면(예를 들어, avgNormalizedDiff > 0.8), 신뢰성이 떨어진다고 판단하여 제외
        if (avgNormalizedDiff < minNormalizedDistance && avgNormalizedDiff < 0.8) {
            minNormalizedDistance = avgNormalizedDiff
            bestCandidate = ref
        }

        // 디버깅 로그
        println("Ref (${ref.lat}, ${ref.lon}) avgNormalizedDiff: $avgNormalizedDiff")
    }

    return bestCandidate
}
