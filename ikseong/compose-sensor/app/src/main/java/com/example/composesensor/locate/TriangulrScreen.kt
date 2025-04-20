package com.example.composesensor.locate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TriangulationRoute(
    padding: PaddingValues
) {
    TriangulationScreen(padding = padding)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TriangulationScreen(
    padding: PaddingValues
) {
    val context = LocalContext.current
    val measurementManager = remember { MeasurementManager() }

    // 측정 모드 (RTT 또는 RSSI)
    var measurementMode by remember { mutableStateOf(MeasurementMode.AUTO) }
    
    // RTT 관련 상태값
    var rttSupported by remember { mutableStateOf(false) }
    var rttAvailable by remember { mutableStateOf(false) }
    var rttEnabled by remember { mutableStateOf(true) } // RTT 모드 ON/OFF 스위치
    var rttCapableAPs by remember { mutableStateOf<List<String>>(emptyList()) } // RTT 가능한 AP 목록
    var totalScannedAPs by remember { mutableIntStateOf(0) } // 스캔된 총 AP 수

    // 필요한 모든 권한 정의
    val requiredPermissions = remember {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES
        )
    }

    // 권한 상태 관리
    var allPermissionsGranted by remember { mutableStateOf(false) }
    
    // 다중 권한 요청을 위한 런처
    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            allPermissionsGranted = permissions.all { it.value }
        }
    )

    // 권한 상태 확인
    LaunchedEffect(Unit) {
        val permissionResults = requiredPermissions.map { permission ->
            permission to (ActivityCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED)
        }.toMap()
        
        allPermissionsGranted = permissionResults.all { it.value }
        
        if (!allPermissionsGranted) {
            multiplePermissionLauncher.launch(requiredPermissions)
        }
        
        // RTT 지원 여부 확인
        rttSupported = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)
    }

    var status by remember { mutableStateOf("측정 대기중") }
    var position by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(allPermissionsGranted, measurementMode, rttEnabled) {
        try {
            if (!allPermissionsGranted) {
                status = "권한이 필요합니다. 모든 위치 권한을 허용해주세요."
                Log.e("PositionLog", "권한 부족: 필요한 모든 권한이 허용되지 않았습니다")
                return@LaunchedEffect
            }

            // RTT 요청 시도
            if ((measurementMode == MeasurementMode.RTT || measurementMode == MeasurementMode.AUTO) && rttEnabled) {
                try {
                    val wifiRttManager = context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager
                    rttAvailable = wifiRttManager.isAvailable
                    Log.d("PositionLog", "WifiRttManager 초기화 완료")
                    Log.d("PositionLog", "WifiRttManager availability: ${wifiRttManager.isAvailable}")
                    // 반복 측정을 위한 루프
                    while (true) {
                        if (!rttEnabled) {
                            Log.d("PositionLog", "RTT 모드가 OFF로 변경되어 RSSI 모드로 전환")
                            break
                        }
                        
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            status = "위치 권한이 없습니다. 설정에서 권한을 확인해주세요."
                            Log.e("PositionLog", "위치 권한 부족: ACCESS_FINE_LOCATION 권한이 없습니다")
                            delay(5000L)
                            continue
                        }
                        
                        // RTT 가능한 AP 스캔
                        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        wifiManager.startScan()
                        val scanResults = wifiManager.scanResults
                        totalScannedAPs = scanResults.size
                        val rttAPs = scanResults.filter { it.is80211mcResponder }
                        
                        // RTT 가능한 AP 목록 업데이트
                        rttCapableAPs = rttAPs.map { 
                            "${it.SSID.ifEmpty { "무명" }} (${it.BSSID}) - RSSI: ${it.level}dBm" 
                        }

                        // RTT 요청 객체 생성
                        val rangingRequest: RangingRequest? = buildRangingRequest(context, measurementManager)
                        if (rangingRequest == null) {
                            // RTT 요청 실패 시 RSSI 모드로 전환
                            if (measurementMode == MeasurementMode.AUTO) {
                                status = "RTT 지원 AP 없음, RSSI 모드로 전환"
                                Log.w("PositionLog", "RTT 요청 생성 실패, RSSI 기반 측정으로 전환")
                                measurementMode = MeasurementMode.RSSI
                                break // RTT 루프 종료
                            } else {
                                status = "RTT 지원 AP 없음"
                                Log.e("PositionLog", "RTT 요청 생성 실패: 지원 AP를 찾을 수 없음")
                                delay(5000L)
                                continue
                            }
                        }

                        // RTT 측정을 위한 Executor
                        val executor = context.mainExecutor
                        
                        try {
                            Log.d("PositionLog", "RTT 측정 시작")
                            wifiRttManager.startRanging(rangingRequest, executor, object : RangingResultCallback() {
                                override fun onRangingResults(results: List<RangingResult>) {
                                    Log.d("PositionLog", "RTT 측정 결과 수신: ${results.size}개")
                                    
                                    // 측정 결과 처리
                                    val validResults = results.filter { result ->
                                        result.status == RangingResult.STATUS_SUCCESS
                                    }
                                    
                                    Log.d("PositionLog", "유효한 측정 결과: ${validResults.size}개")
                                    
                                    // 측정 결과가 있을 경우 처리
                                    if (validResults.isNotEmpty()) {
                                        processRttResults(validResults, measurementManager)
                                        calculatePosition(measurementManager) { newStatus, newPosition ->
                                            status = newStatus
                                            position = newPosition
                                        }
                                    } else {
                                        status = "유효한 RTT 측정 결과 없음"
                                        Log.w("PositionLog", "유효한 RTT 측정 결과가 없습니다")
                                    }
                                }
                                
                                override fun onRangingFailure(code: Int) {
                                    status = "RTT 측정 실패: $code"
                                    Log.e("PositionLog", "RTT 측정 실패: 코드 $code")
                                    
                                    // 측정 실패 시 자동 모드에서는 RSSI로 전환
                                    if (measurementMode == MeasurementMode.AUTO) {
                                        measurementMode = MeasurementMode.RSSI
                                        Log.d("PositionLog", "RTT 측정 실패로 RSSI 모드로 전환")
                                    }
                                }
                            })
                        } catch (e: SecurityException) {
                            status = "보안 예외: ${e.message}"
                            Log.e("PositionLog", "보안 예외 발생: ${e.message}", e)
                        } catch (e: Exception) {
                            status = "예외 발생: ${e.message}"
                            Log.e("PositionLog", "예외 발생: ${e.message}", e)
                        }
                        
                        delay(5000L)
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    Log.d("PositionLog", "RTT 측정 코루틴이 취소되었습니다: ${e.message}")
                    throw e  // 취소 예외는 다시 던져서 상위 코루틴에 전파
                } catch (e: Exception) {
                    Log.e("PositionLog", "RTT 초기화 실패: ${e.message}", e)
                    if (measurementMode == MeasurementMode.AUTO) {
                        measurementMode = MeasurementMode.RSSI
                        Log.d("PositionLog", "RTT 초기화 실패로 RSSI 모드로 전환")
                    }
                }
            }
            
            // RSSI 기반 측정 (RTT가 실패했거나 RSSI 모드로 설정된 경우)
            if (measurementMode == MeasurementMode.RSSI || !rttEnabled) {
                status = "RSSI 기반 측정 시작"
                Log.d("PositionLog", "RSSI 기반 측정 시작")
                
                while (true) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        status = "위치 권한이 없습니다. 설정에서 권한을 확인해주세요."
                        Log.e("PositionLog", "위치 권한 부족: ACCESS_FINE_LOCATION 권한이 없습니다")
                        delay(5000L)
                        continue
                    }
                    
                    try {
                        // WiFi 스캔 먼저 실행
                        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        wifiManager.startScan()
                        val scanResults = wifiManager.scanResults
                        totalScannedAPs = scanResults.size
                        
                        // RSSI 측정 수행
                        val results = performRssiMeasurement(context, measurementManager)
                        
                        if (results.isNotEmpty()) {
                            calculatePosition(measurementManager) { newStatus, newPosition ->
                                status = newStatus
                                position = newPosition
                            }
                        } else {
                            status = "RSSI 측정 결과 없음"
                            Log.w("PositionLog", "RSSI 측정 결과가 없습니다")
                        }
                    } catch (e: Exception) {
                        status = "RSSI 측정 예외: ${e.message}"
                        Log.e("PositionLog", "RSSI 측정 중 예외 발생: ${e.message}", e)
                    }
                    
                    delay(5000L)
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            Log.d("PositionLog", "측정 코루틴이 취소되었습니다")
            // 취소 예외는 정상으로 처리
        } catch (e: Exception) {
            Log.e("PositionLog", "측정 중 예상치 못한 오류 발생: ${e.message}", e)
            status = "오류 발생: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
    ) {
        // 상단 RTT 제어 섹션
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (rttSupported && rttAvailable) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RTT 상태 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (rttSupported && rttAvailable) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    // RTT 모드 ON/OFF 스위치
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RTT 모드",
                            fontSize = 14.sp,
                            color = if (rttSupported && rttAvailable) 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Switch(
                            checked = rttEnabled,
                            onCheckedChange = { 
                                rttEnabled = it 
                                if (!it && measurementMode == MeasurementMode.RTT) {
                                    measurementMode = MeasurementMode.RSSI
                                }
                            },
                            enabled = rttSupported && rttAvailable
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val rttStatusText = when {
                    !rttSupported -> "이 기기는 WiFi RTT를 지원하지 않습니다"
                    !rttAvailable -> "WiFi RTT가 현재 사용 불가능합니다"
                    else -> "WiFi RTT 사용 가능"
                }
                
                Text(
                    text = rttStatusText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = if (rttSupported && rttAvailable) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // AP 스캔 결과 정보 추가
                Text(
                    text = "스캔된 AP: $totalScannedAPs 개, RTT 지원 AP: ${rttCapableAPs.size}개",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (rttSupported && rttAvailable) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 측정 모드 선택 간략화 (기존 카드에서 옮겨옴)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "측정 모드:",
                        fontSize = 12.sp,
                        color = if (rttSupported && rttAvailable) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 측정 모드 선택 버튼들을 가로로 배치 (라디오 버튼)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 자동 모드
                        androidx.compose.material3.RadioButton(
                            selected = measurementMode == MeasurementMode.AUTO,
                            onClick = { measurementMode = MeasurementMode.AUTO },
                            enabled = rttEnabled || measurementMode != MeasurementMode.RTT,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "자동",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        // RTT 전용 모드
                        androidx.compose.material3.RadioButton(
                            selected = measurementMode == MeasurementMode.RTT,
                            onClick = { if (rttEnabled) measurementMode = MeasurementMode.RTT },
                            enabled = rttEnabled,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "RTT",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        // RSSI 전용 모드
                        androidx.compose.material3.RadioButton(
                            selected = measurementMode == MeasurementMode.RSSI,
                            onClick = { measurementMode = MeasurementMode.RSSI },
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "RSSI",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        
        // 지도 및 위치 정보 섹션
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 지도 (화면의 2/3)
            MyNaverMap(
                modifier = Modifier
                    .weight(2f)
                    .height(250.dp),
                latLng = position?.let { LatLng(it.first.toDouble(), it.second.toDouble()) }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 위치 정보 카드 (화면의 1/3)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Text(
                        text = "위치 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 현재 위치 정보 표시
                    position?.let { loc ->
                        // 위도 경도 정보를 카드로 강조 표시
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = "위도: ${loc.first}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "경도: ${loc.second}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } ?: Text(
                        text = "위치 측정 중...",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 상태 정보 표시
                    Text(
                        text = "상태",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        // RTT 가능한 AP 목록 카드
        if (rttCapableAPs.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = "RTT 지원 액세스 포인트 (${rttCapableAPs.size}개)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 스크롤 가능한 AP 목록
                    Column(
                        modifier = Modifier
                            .heightIn(max = 150.dp) // 최대 높이 제한
                            .verticalScroll(rememberScrollState())
                    ) {
                        rttCapableAPs.forEach { ap ->
                            Text(
                                text = ap,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
        
        // 현재 위도/경도 정보 카드 (맨 아래에 크게 표시)
        position?.let { loc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "현재 좌표",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "위도",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${loc.first}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        
                        Column {
                            Text(
                                text = "경도",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${loc.second}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

// 위치 정보 아이템 컴포넌트
@Composable
private fun LocationInfoItem2(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// AP 위치 정보를 더 정확하게 관리하기 위한 클래스
data class AccessPoint(
    val id: String,
    val bssid: String,
    val x: Double,  // 미리 정해진 로컬 좌표 (미터 단위)
    val y: Double,
    val z: Double = 0.0,  // 3D 좌표 지원
    val confidence: Double = 1.0  // AP 위치의 신뢰도 (0.0 ~ 1.0)
)

// 측정 결과를 저장하고 평균화하기 위한 클래스
data class RangingMeasurement(
    val bssid: String,
    val distance: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val rssi: Int,
    val confidence: Double = 1.0  // 측정의 신뢰도
)

// 측정 결과를 관리하는 클래스
class MeasurementManager(
    private val maxHistorySize: Int = 10,
    private val maxAgeMs: Long = 30000  // 30초
) {
    private val measurements = mutableMapOf<String, MutableList<RangingMeasurement>>()
    
    // 동적으로 관리될 AP 목록
    private val dynamicAPs = mutableListOf<AccessPoint>()

    fun addMeasurement(measurement: RangingMeasurement) {
        val list = measurements.getOrPut(measurement.bssid) { mutableListOf() }
        list.add(measurement)
        
        // 오래된 측정값 제거
        val now = System.currentTimeMillis()
        list.removeAll { now - it.timestamp > maxAgeMs }
        
        // 최대 개수 제한
        if (list.size > maxHistorySize) {
            list.removeAt(0)
        }
    }

    fun getAverageDistance(bssid: String): Double? {
        val measurements = measurements[bssid] ?: return null
        if (measurements.isEmpty()) return null
        
        // 가중 평균 계산 (신뢰도 기반)
        var totalWeight = 0.0
        var weightedSum = 0.0
        
        measurements.forEach { measurement ->
            val weight = measurement.confidence
            totalWeight += weight
            weightedSum += measurement.distance * weight
        }
        
        return if (totalWeight > 0) weightedSum / totalWeight else null
    }
    
    // AP 정보 동적 업데이트 메서드
    fun updateAccessPoint(id: String, bssid: String, x: Double, y: Double, z: Double = 0.0, confidence: Double = 1.0) {
        val existingIndex = dynamicAPs.indexOfFirst { it.bssid == bssid }
        
        if (existingIndex >= 0) {
            // 기존 AP 업데이트
            dynamicAPs[existingIndex] = AccessPoint(id, bssid, x, y, z, confidence)
            Log.d("PositionLog", "AP 정보 업데이트: $id ($bssid), 위치($x, $y)")
        } else {
            // 새 AP 추가
            dynamicAPs.add(AccessPoint(id, bssid, x, y, z, confidence))
            Log.d("PositionLog", "새 AP 추가: $id ($bssid), 위치($x, $y)")
        }
    }
    
    // 현재 알려진 AP 목록 반환
    fun getKnownAPs(): List<AccessPoint> {
        return dynamicAPs
    }

    fun getAverageRssi(bssid: String): Int? {
        val measurements = this.measurements[bssid] ?: return null
        if (measurements.isEmpty()) return null
        
        var totalRssi = 0
        measurements.forEach { measurement ->
            totalRssi += measurement.rssi
        }
        
        return totalRssi / measurements.size
    }
    
    // 모든 AP 제거
    fun clearAllAPs() {
        dynamicAPs.clear()
        Log.d("PositionLog", "모든 AP 정보가 초기화되었습니다")
    }
}

// RSSI를 거리로 변환하는 함수 (RTT가 지원되지 않는 환경에서 사용)
fun rssiToDistance(rssi: Int, referenceRssi: Int = -50, referenceDistance: Double = 1.0): Double {
    // 경로 손실 지수 - 실내 환경에 맞게 조정
    val pathLossExponent = 2.0
    
    // 거리 계산 (경로 손실 모델, 단순화된 버전)
    return referenceDistance * 10.0.pow((referenceRssi - rssi) / (10 * pathLossExponent))
}

private fun buildRangingRequest(context: Context, measurementManager: MeasurementManager): RangingRequest? {
    // RTT 최대 지원 개수 제한
    val maxRttDevices = RangingRequest.getMaxPeers()
    Log.d("PositionLog", "RTT 최대 지원 AP 개수: $maxRttDevices")
    
    // WiFi 스캔 결과 가져오기
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    if (!wifiManager.isWifiEnabled) {
        Log.e("PositionLog", "WiFi가 비활성화되어 있습니다")
        return null
    }
    
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e("PositionLog", "위치 권한이 없습니다")
        return null
    }
    
    val scanResults = wifiManager.scanResults
    Log.d("PositionLog", "스캔된 AP 개수: ${scanResults.size}")
    
    // 모든 AP 정보 로깅
    Log.d("PositionLog", "==== 스캔된 모든 AP 정보 ====")
    scanResults.forEachIndexed { index, result ->
        Log.d("PositionLog", "AP[$index]: SSID='${result.SSID}', BSSID=${result.BSSID}, " +
                "RSSI=${result.level}dBm, 주파수=${result.frequency}MHz, " +
                "RTT지원=${result.is80211mcResponder}, 채널폭=${result.channelWidth}")
    }
    
    // 802.11mc 지원 AP 필터링
    val rttCapableAps = scanResults
        .filter { result ->
        result.is80211mcResponder
    }
    
    Log.d("PositionLog", "==== RTT 지원 AP 정보 ====")
    Log.d("PositionLog", "RTT 지원 AP 개수: ${rttCapableAps.size}")
    rttCapableAps.forEachIndexed { index, result ->
        Log.d("PositionLog", "RTT-AP[$index]: SSID='${result.SSID}', BSSID=${result.BSSID}, " +
                "RSSI=${result.level}dBm, 주파수=${result.frequency}MHz, RTT 지원 여부=${result.is80211mcResponder}")
    }
    
    if (rttCapableAps.isEmpty()) {
        Log.w("PositionLog", "RTT 지원 AP가 없어 일반 AP를 사용합니다")
        
        val requestBuilder = RangingRequest.Builder()
        var count = 0
        
        Log.d("PositionLog", "==== 동적 AP 할당 (RSSI 기반) ====")
        
        // 신호 강도 기준으로 정렬
        val strongestAPs = scanResults
            .sortedByDescending { it.level }
            .take(maxRttDevices.coerceAtMost(5))  // 최대 5개 또는 최대 지원 개수
        
        // 신호 강도 기준으로 상위 AP 추가
        strongestAPs.forEachIndexed { index, result ->
            requestBuilder.addAccessPoint(result)
            count++
            
            // RSSI 기반 거리 계산
            val estimatedDistance = rssiToDistance(result.level)
            Log.d("PositionLog", "AP 추가: ${result.BSSID}, RSSI=${result.level}dBm, 추정거리=${estimatedDistance}m")
            
            // 동적으로 AP 정보 추가 - 신호 강도에 따른 상대적 위치 배치
            val position = calculateRelativePosition(result, index, strongestAPs)
            
            // SSID가 없으면 BSSID의 일부를 사용
            val apName = result.SSID.ifEmpty { "AP-${result.BSSID.takeLast(5)}" }
            measurementManager.updateAccessPoint(
                id = apName,
                bssid = result.BSSID,
                x = position.first,
                y = position.second,
                confidence = calculateRssiConfidence(result.level)
            )
            
            Log.d("PositionLog", "동적 AP 추가: $apName (${result.BSSID}), 위치(${position.first}, ${position.second})")
        }
        
        return if (count > 0) {
            Log.d("PositionLog", "총 ${count}개의 AP로 RTT 요청 생성")
            requestBuilder.build()
        } else {
            Log.e("PositionLog", "RTT 요청 생성 실패: 사용 가능한 AP 없음")
            null
        }
    }
    
    // RTT 지원 AP가 있는 경우
    val requestBuilder = RangingRequest.Builder()
    var count = 0
    
    Log.d("PositionLog", "==== RTT 요청 구성 ====")
    
    // RTT 지원 AP들 중 신호가 강한 순으로 정렬하여 선택
    val selectedRttAps = rttCapableAps
        .sortedByDescending { it.level }
        .take(maxRttDevices.coerceAtMost(3))
    
    // 첫 번째 AP는 원점(0,0)에 배치
    if (selectedRttAps.isNotEmpty()) {
        val firstAp = selectedRttAps[0]
        requestBuilder.addAccessPoint(firstAp)
        count++
        
        // SSID가 없으면 BSSID의 일부를 사용
        val apName = firstAp.SSID.ifEmpty { "RTT-${firstAp.BSSID.takeLast(5)}" }
        
        // 첫 번째 AP는 원점에 배치
        measurementManager.updateAccessPoint(
            id = apName,
            bssid = firstAp.BSSID,
            x = 0.0,
            y = 0.0,
            confidence = 1.0  // 기준점이므로 최대 신뢰도
        )
        
        Log.d("PositionLog", "첫 번째 AP 추가: $apName (${firstAp.BSSID}), 위치(0.0, 0.0)")
        
        // 두 번째 AP가 있는 경우, 첫 번째 AP와의 거리를 기반으로 배치
        if (selectedRttAps.size > 1) {
            val secondAp = selectedRttAps[1]
            requestBuilder.addAccessPoint(secondAp)
            count++
            
            // 단순 RSSI 기반 거리 계산
            val distance = rssiToDistance(secondAp.level)
            
            // SSID가 없으면 BSSID의 일부를 사용
            val secondApName = secondAp.SSID.ifEmpty { "RTT-${secondAp.BSSID.takeLast(5)}" }
            
            // 두 번째 AP는 x축 상에 배치, 거리는 RSSI 기반 추정치 사용
            measurementManager.updateAccessPoint(
                id = secondApName,
                bssid = secondAp.BSSID,
                x = distance,  // 순수 RSSI 기반 거리 사용
                y = 0.0,
                confidence = 1.0  // 단순화를 위해 모든 AP에 동일한 신뢰도 부여
            )
            
            Log.d("PositionLog", "두 번째 AP 추가: $secondApName (${secondAp.BSSID}), 위치($distance, 0.0)")
            
            // 세 번째 AP가 있는 경우
            if (selectedRttAps.size > 2) {
                val thirdAp = selectedRttAps[2]
                requestBuilder.addAccessPoint(thirdAp)
                count++
                
                // 단순 RSSI 기반 거리 계산
                val thirdDistance = rssiToDistance(thirdAp.level)
                
                // 세 번째 AP는 삼각형을 형성하도록 배치
                // 단순하게 60도 각도로 배치 (직접 삼각법 사용)
                val thirdX = thirdDistance / 2  // cos(60°) * distance ≈ distance/2
                val thirdY = thirdDistance * 0.866  // sin(60°) * distance ≈ distance*0.866
                
                // SSID가 없으면 BSSID의 일부를 사용
                val thirdApName = thirdAp.SSID.ifEmpty { "RTT-${thirdAp.BSSID.takeLast(5)}" }
                
                // 세 번째 AP는 삼각형 형태로 배치
                measurementManager.updateAccessPoint(
                    id = thirdApName,
                    bssid = thirdAp.BSSID,
                    x = thirdX,
                    y = thirdY,
                    confidence = 1.0
                )
                
                Log.d("PositionLog", "세 번째 AP 추가: $thirdApName (${thirdAp.BSSID}), 위치($thirdX, $thirdY)")
            }
        }
    }
    
    return if (count > 0) {
        Log.d("PositionLog", "RTT 요청 생성 완료: 총 ${count}개의 RTT 지원 AP 포함")
        requestBuilder.build()
    } else {
        Log.e("PositionLog", "RTT 요청 생성 실패: 사용 가능한 AP 없음")
        null
    }
}

// 측정 신뢰도 계산
private fun calculateConfidence(result: RangingResult): Double {
    var confidence = 1.0
    
    // RSSI 기반 신뢰도
    val rssiConfidence = when {
        result.rssi >= -50 -> 1.0
        result.rssi >= -70 -> 0.8
        result.rssi >= -80 -> 0.6
        else -> 0.4
    }
    
    // 거리 기반 신뢰도
    val distanceConfidence = when {
        result.distanceMm < 1000 -> 1.0
        result.distanceMm < 5000 -> 0.8
        result.distanceMm < 10000 -> 0.6
        else -> 0.4
    }
    
    confidence *= rssiConfidence
    confidence *= distanceConfidence
    
    return confidence
}

// AP와 측정 결과 매칭
private fun matchAPsWithMeasurements(
    measurements: Map<String, Double>,
    measurementManager: MeasurementManager
): List<Triple<AccessPoint, Double, Double>> {
    val result = mutableListOf<Triple<AccessPoint, Double, Double>>()
    
    // 현재 동적으로 할당된 AP 목록 가져오기
    val dynamicAPs = measurementManager.getKnownAPs()
    
    Log.d("PositionLog", "동적으로 할당된 AP 정보: ${dynamicAPs.size}개")
    dynamicAPs.forEach { ap ->
        Log.d("PositionLog", "AP 정보: ${ap.id} (${ap.bssid}), 좌표(${ap.x}, ${ap.y})")
    }
    
    // 매칭되는 AP 찾기
    val matchedAPs = measurements.mapNotNull { (bssid, distance) ->
        dynamicAPs.find { it.bssid.equals(bssid, ignoreCase = true) }?.let { ap ->
            Triple(ap, distance, calculateConfidence(ap, distance))
        }
    }
    
    if (matchedAPs.isNotEmpty()) {
        result.addAll(matchedAPs)
        Log.d("PositionLog", "매칭된 AP: ${matchedAPs.size}개")
    } else {
        // 매칭되는 AP가 없으면 측정된 AP들에게 동적으로 위치 할당
        Log.d("PositionLog", "매칭되는 AP가 없어서 새로 동적 위치 할당")
        
        // 각 측정 AP를 원형으로 배치
        measurements.entries.forEachIndexed { index, (bssid, distance) ->
            val angle = (index * Math.PI * 2 / measurements.size)
            val position = Pair(
                distance * Math.cos(angle),
                distance * Math.sin(angle)
            )
            
            // 가상 AP 생성
            val virtualAP = AccessPoint(
                id = "DynamicAP$index",
                bssid = bssid,
                x = position.first,
                y = position.second,
                confidence = 0.7
            )
            
            // 측정 매니저에 AP 정보 추가
            measurementManager.updateAccessPoint(
                id = virtualAP.id,
                bssid = virtualAP.bssid,
                x = virtualAP.x,
                y = virtualAP.y,
                confidence = 0.7
            )
            
            Log.d("PositionLog", "새로운 동적 AP 할당: $bssid -> 위치(${position.first}, ${position.second})")
            result.add(Triple(virtualAP, distance, 0.7))
        }
    }
    
    // 삼각측량에 필요한 최소 3개의 AP 확보
    if (result.size == 2) {
        // 2개만 있는 경우 가상의 AP 추가
        val ap1 = result[0].first
        val ap2 = result[1].first
        val dist1 = result[0].second
        val dist2 = result[1].second
        
        // 두 AP의 중점에서 수직 방향으로 배치
        val midX = (ap1.x + ap2.x) / 2
        val midY = (ap1.y + ap2.y) / 2
        val dx = ap2.x - ap1.x
        val dy = ap2.y - ap1.y
        // 두 점을 잇는 벡터에 수직인 단위 벡터에 적당한 거리 곱함
        val perpDistance = (dist1 + dist2) / 2
        val virtualX = midX - dy / Math.sqrt(dx*dx + dy*dy) * perpDistance
        val virtualY = midY + dx / Math.sqrt(dx*dx + dy*dy) * perpDistance
        
        // 가상 AP 생성
        val virtualAP = AccessPoint(
            id = "VirtualAP",
            bssid = "virtual:ap:${System.currentTimeMillis()}",
            x = virtualX,
            y = virtualY,
            confidence = 0.6
        )
        
        // 측정 매니저에 AP 정보 추가
        measurementManager.updateAccessPoint(
            id = virtualAP.id,
            bssid = virtualAP.bssid,
            x = virtualAP.x,
            y = virtualAP.y,
            confidence = 0.6
        )
        
        result.add(Triple(virtualAP, perpDistance, 0.6))
        Log.d("PositionLog", "가상 AP 추가: ${virtualAP.id}, 위치=(${virtualAP.x}, ${virtualAP.y}), 거리=${perpDistance}m")
    }
    
    return result.sortedByDescending { it.third }  // 신뢰도 순으로 정렬
}

// AP와 측정 거리의 신뢰도 계산
private fun calculateConfidence(ap: AccessPoint, distance: Double): Double {
    var confidence = ap.confidence
    
    // 거리가 너무 멀면 신뢰도 감소
    if (distance > 50.0) {  // 50m 이상
        confidence *= 0.5
    }
    
    return confidence
}

// 삼각측량 함수 개선
fun triangulation(
    ap1: AccessPoint, r1: Double,
    ap2: AccessPoint, r2: Double,
    ap3: AccessPoint, r3: Double
): Pair<Double, Double>? {
    Log.d("PositionLog", "삼각측량 계산 시작")
    Log.d("PositionLog", "AP1(${ap1.id}): 위치(${ap1.x}, ${ap1.y}), 거리: ${r1}m")
    Log.d("PositionLog", "AP2(${ap2.id}): 위치(${ap2.x}, ${ap2.y}), 거리: ${r2}m")
    Log.d("PositionLog", "AP3(${ap3.id}): 위치(${ap3.x}, ${ap3.y}), 거리: ${r3}m")
    
    // 거리 스케일 조정 (너무 큰 거리는 스케일링)
    val scaleFactor = if (r1 > 1000.0 || r2 > 1000.0 || r3 > 1000.0) {
        // 거리가 15km 정도라면 적절한 스케일로 조정
        20.0
    } else {
        1.0
    }
    
    // 스케일 적용된 거리
    val scaledR1 = r1 / scaleFactor
    val scaledR2 = r2 / scaleFactor
    val scaledR3 = r3 / scaleFactor
    
    Log.d("PositionLog", "스케일 적용 (${scaleFactor}): r1=${scaledR1}m, r2=${scaledR2}m, r3=${scaledR3}m")
    
    // AP 위치의 신뢰도 고려
    val w1 = ap1.confidence
    val w2 = ap2.confidence
    val w3 = ap3.confidence
    
    val (x1, y1) = Pair(ap1.x, ap1.y)
    val (x2, y2) = Pair(ap2.x, ap2.y)
    val (x3, y3) = Pair(ap3.x, ap3.y)

    // 가중치를 고려한 방정식
    val A = 2 * w1 * (x2 - x1)
    val B = 2 * w1 * (y2 - y1)
    val C = w1 * (scaledR1.pow(2) - scaledR2.pow(2) - x1.pow(2) + x2.pow(2) - y1.pow(2) + y2.pow(2))

    val D = 2 * w2 * (x3 - x1)
    val E = 2 * w2 * (y3 - y1)
    val F = w2 * (scaledR1.pow(2) - scaledR3.pow(2) - x1.pow(2) + x3.pow(2) - y1.pow(2) + y3.pow(2))

    val denominator = A * E - B * D
    if (denominator == 0.0) {
        Log.e("PositionLog", "삼각측량 실패: 분모가 0입니다")
        return null
    }

    val x = (C * E - F * B) / denominator
    val y = (A * F - C * D) / denominator
    
    Log.d("PositionLog", "삼각측량 중간 계산 값:")
    Log.d("PositionLog", "A=$A, B=$B, C=$C, D=$D, E=$E, F=$F")
    Log.d("PositionLog", "분모=$denominator, x=$x, y=$y")
    
    // 좌표계 변환 - 로컬 좌표를 위도 경도로 변환
    val latLng = localToGps(x, y)
    Log.d("PositionLog", "최종 위치: 로컬(${x}, ${y}) -> 위도/경도(${latLng.first}, ${latLng.second})")
    
    return latLng
}

// 로컬 좌표를 GPS 좌표(위도,경도)로 변환
fun localToGps(x: Double, y: Double): Pair<Double, Double> {
    // 건국대학교 위치를 기준으로 (예시)
    val baseLatitude = 37.540706  // 기준 위도
    val baseLongitude = 127.079590  // 기준 경도
    
    // 지구 반경 (미터)
    val earthRadius = 6378137.0
    
    // 위도 1도의 거리 (미터)
    val metersPerLatDegree = (Math.PI / 180) * earthRadius
    
    // 경도 1도의 거리 (미터, 위도에 따라 달라짐)
    val metersPerLngDegree = metersPerLatDegree * Math.cos(Math.toRadians(baseLatitude))
    
    // 위도, 경도 계산
    val latOffset = y / metersPerLatDegree
    val lngOffset = x / metersPerLngDegree
    
    val latitude = baseLatitude + latOffset
    val longitude = baseLongitude + lngOffset
    
    return Pair(latitude, longitude)
}

// 삼각측량 수행
private fun performTriangulation(
    matchedAPs: List<Triple<AccessPoint, Double, Double>>
): Pair<Double, Double>? {
    if (matchedAPs.size < 2) {
        Log.e("PositionLog", "삼각측량에 필요한 AP가 부족합니다: ${matchedAPs.size}/2")
        return null
    }
    
    // 2개만 있는 경우와 3개 이상 있는 경우 처리
    if (matchedAPs.size >= 3) {
        // 신뢰도가 가장 높은 3개의 AP 선택
        val selectedAPs = matchedAPs.take(3)
        
        Log.d("PositionLog", "삼각측량 입력값 (3개 AP):")
        Log.d("PositionLog", "AP1: ${selectedAPs[0].first.id}, 위치(${selectedAPs[0].first.x}, ${selectedAPs[0].first.y}), 거리: ${selectedAPs[0].second}m")
        Log.d("PositionLog", "AP2: ${selectedAPs[1].first.id}, 위치(${selectedAPs[1].first.x}, ${selectedAPs[1].first.y}), 거리: ${selectedAPs[1].second}m")
        Log.d("PositionLog", "AP3: ${selectedAPs[2].first.id}, 위치(${selectedAPs[2].first.x}, ${selectedAPs[2].first.y}), 거리: ${selectedAPs[2].second}m")
        
        return triangulation(
            selectedAPs[0].first, selectedAPs[0].second,
            selectedAPs[1].first, selectedAPs[1].second,
            selectedAPs[2].first, selectedAPs[2].second
        )
    } else {
        // 2개의 AP만 있는 경우는 간단한 위치 계산
        val ap1 = matchedAPs[0].first
        val r1 = matchedAPs[0].second
        val ap2 = matchedAPs[1].first
        val r2 = matchedAPs[1].second
        
        Log.d("PositionLog", "2개 AP 기반 위치 계산:")
        Log.d("PositionLog", "AP1: ${ap1.id}, 위치(${ap1.x}, ${ap1.y}), 거리: ${r1}m")
        Log.d("PositionLog", "AP2: ${ap2.id}, 위치(${ap2.x}, ${ap2.y}), 거리: ${r2}m")
        
        // 두 원의 교점 찾기 (단순화된 버전)
        return calculatePositionWith2AP(ap1, r1, ap2, r2)
    }
}

// 두 개의 AP만 있을 때 위치 계산
private fun calculatePositionWith2AP(
    ap1: AccessPoint, r1: Double,
    ap2: AccessPoint, r2: Double
): Pair<Double, Double> {
    Log.d("PositionLog", "2개 AP로 위치 계산 시작")
    
    // 거리 스케일 조정 (너무 큰 거리는 스케일링)
    val scaleFactor = if (r1 > 1000.0 || r2 > 1000.0) {
        // 거리가 15km 정도라면 적절한 스케일로 조정
        20.0
    } else {
        1.0
    }
    
    // 스케일 적용된 거리
    val scaledR1 = r1 / scaleFactor
    val scaledR2 = r2 / scaleFactor
    
    Log.d("PositionLog", "스케일 적용 (${scaleFactor}): r1=${scaledR1}m, r2=${scaledR2}m")
    
    val (x1, y1) = Pair(ap1.x, ap1.y)
    val (x2, y2) = Pair(ap2.x, ap2.y)
    
    // 두 AP 사이의 거리 계산
    val d = sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
    
    // 두 AP 사이의 거리가 각 원의 반지름의 합보다 크거나, 차보다 작으면 교점이 없음
    if (d > scaledR1 + scaledR2 || d < abs(scaledR1 - scaledR2)) {
        // 겹치는 교점이 없는 경우, 두 AP 사이의 가중 평균 위치를 사용
        val weight1 = 1.0 / scaledR1
        val weight2 = 1.0 / scaledR2
        val totalWeight = weight1 + weight2
        
        val x = (weight1 * x1 + weight2 * x2) / totalWeight
        val y = (weight1 * y1 + weight2 * y2) / totalWeight
        
        Log.d("PositionLog", "두 원 교점 없음, 가중 평균 사용: x=$x, y=$y")
        
        // 좌표계 변환 - 로컬 좌표를 위도 경도로 변환
        val latLng = localToGps(x, y)
        Log.d("PositionLog", "최종 위치: 로컬(${x}, ${y}) -> 위도/경도(${latLng.first}, ${latLng.second})")
        
        return latLng
    }
    
    // 두 원의 교점 계산
    val a = (scaledR1.pow(2) - scaledR2.pow(2) + d.pow(2)) / (2 * d)
    val h = sqrt(scaledR1.pow(2) - a.pow(2))
    
    val x0 = x1 + a * (x2 - x1) / d
    val y0 = y1 + a * (y2 - y1) / d
    
    // 두 교점 계산
    val x3 = x0 + h * (y2 - y1) / d
    val y3 = y0 - h * (x2 - x1) / d
    
    val x4 = x0 - h * (y2 - y1) / d
    val y4 = y0 + h * (x2 - x1) / d
    
    // 두 교점 중 원점(0,0)에 가까운 점 선택
    val dist3 = sqrt(x3.pow(2) + y3.pow(2))
    val dist4 = sqrt(x4.pow(2) + y4.pow(2))
    
    val (x, y) = if (dist3 < dist4) Pair(x3, y3) else Pair(x4, y4)
    
    Log.d("PositionLog", "두 원 교점 계산: x=$x, y=$y")
    
    // 좌표계 변환 - 로컬 좌표를 위도 경도로 변환
    val latLng = localToGps(x, y)
    Log.d("PositionLog", "최종 위치: 로컬(${x}, ${y}) -> 위도/경도(${latLng.first}, ${latLng.second})")
    
    return latLng
}

// 위치 계산 공통 함수
private fun calculatePosition(
    measurementManager: MeasurementManager,
    updateCallback: (String, Pair<Double, Double>?) -> Unit
) {
    // 각 AP의 평균 거리 계산
    val averageDistances = mutableMapOf<String, Double>()
    val knownAPs = measurementManager.getKnownAPs()
    
    knownAPs.forEach { ap ->
        measurementManager.getAverageDistance(ap.bssid)?.let { distance ->
            averageDistances[ap.bssid] = distance
        }
    }
    
    Log.d("PositionLog", "평균 거리 계산 결과: ${averageDistances.size}개")
    averageDistances.forEach { (bssid, distance) ->
        Log.d("PositionLog", "평균거리: $bssid -> ${distance}m")
    }
    
    if (averageDistances.size < 2) {
        updateCallback("측정 결과 부족 (${averageDistances.size}/2)", null)
        Log.e("PositionLog", "측정 결과가 부족합니다: ${averageDistances.size}/2")
        return
    }
    
    // AP 매칭 및 삼각측량 수행
    val matchedAPs = matchAPsWithMeasurements(averageDistances, measurementManager)
    Log.d("PositionLog", "매칭된 AP: ${matchedAPs.size}개")
    
    matchedAPs.forEach { triple ->
        Log.d("PositionLog", "매칭AP: ${triple.first.id} (${triple.first.bssid}), " +
                "거리: ${triple.second}m, 신뢰도: ${triple.third}, " +
                "좌표: (${triple.first.x}, ${triple.first.y})")
    }
    
    if (matchedAPs.size >= 2) {
        val estimatedPosition = performTriangulation(matchedAPs)
        
        if (estimatedPosition != null) {
            updateCallback("위치 추정 성공: 위도=${estimatedPosition.first}, 경도=${estimatedPosition.second}", estimatedPosition)
            Log.d("PositionLog", "위치 추정 성공: 위도=${estimatedPosition.first}, 경도=${estimatedPosition.second}")
        } else {
            updateCallback("삼각측량 실패", null)
            Log.e("PositionLog", "삼각측량 계산 실패")
        }
    } else {
        updateCallback("AP 매칭 부족 (${matchedAPs.size}/2)", null)
        Log.e("PositionLog", "매칭된 AP가 부족합니다: ${matchedAPs.size}/2")
    }
}

// 측정 모드 정의
enum class MeasurementMode {
    RTT,    // RTT만 사용
    RSSI,   // RSSI만 사용
    AUTO    // 가능하면 RTT, 불가능하면 RSSI
}

// RTT 측정 결과 처리 함수
private fun processRttResults(
    validResults: List<RangingResult>,
    measurementManager: MeasurementManager
) {
    // 상세 측정 결과 로깅
    validResults.forEachIndexed { index, result ->
        // RTT 거리가 너무 큰 경우 RSSI 기반 거리 계산 적용
        val rttDistance = result.distanceMm / 1000.0  // mm to m
        val rssiDistance = rssiToDistance(result.rssi)
        
        // 거리 선택: RTT 거리가 너무 크면 RSSI 거리 사용
        val finalDistance = if (rttDistance > 1000) rssiDistance else rttDistance
        
        Log.d("PositionLog", "AP[$index] MAC: ${result.macAddress}, " +
                "RTT거리: ${rttDistance}m, " +
                "RSSI: ${result.rssi}dBm, " +
                "RSSI거리: ${rssiDistance}m, " +
                "최종거리: ${finalDistance}m, " +
                "표준편차: ${result.distanceStdDevMm/1000.0}m, " +
                "상태: ${result.status}")
        
        // 측정 결과 저장
        val measurement = RangingMeasurement(
            bssid = result.macAddress.toString(),
            distance = finalDistance,  // 선택된 거리 사용
            rssi = result.rssi,
            confidence = calculateConfidence(result)
        )
        
        measurementManager.addMeasurement(measurement)
        Log.d("PositionLog", "측정 저장: ${measurement.bssid}, 거리: ${measurement.distance}m, 신뢰도: ${measurement.confidence}")
    }
}

// RSSI 기반 측정 수행 함수
@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
private fun performRssiMeasurement(
    context: Context,
    measurementManager: MeasurementManager
): List<RangingMeasurement> {
    val results = mutableListOf<RangingMeasurement>()
    
    // WiFi 스캔 결과 가져오기
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    if (!wifiManager.isWifiEnabled) {
        Log.e("PositionLog", "WiFi가 비활성화되어 있습니다")
        return emptyList()
    }
    
    // 사용 가능한 AP 스캔
    val success = wifiManager.startScan()
    if (!success) {
        Log.w("PositionLog", "WiFi 스캔 시작 실패, 이전 결과를 사용합니다")
    }
    
    val scanResults = if (ActivityCompat.checkSelfPermission(
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
        return emptyList()
    }else
        wifiManager.scanResults
    Log.d("PositionLog", "RSSI 측정: 스캔된 AP 개수: ${scanResults.size}")
    
    if (scanResults.isEmpty()) {
        return emptyList()
    }
    
    // 현재 알려진 AP 가져오기
    val knownAPs = measurementManager.getKnownAPs()
    
    // 알려진 AP와 스캔 결과 매칭
    val matchedAPs = scanResults.filter { scanResult ->
        knownAPs.any { it.bssid.equals(scanResult.BSSID, ignoreCase = true) }
    }
    
    Log.d("PositionLog", "RSSI 측정: 알려진 AP와 매칭된 개수: ${matchedAPs.size}")
    
    // 매칭된 AP가 있는 경우
    if (matchedAPs.isNotEmpty()) {
        matchedAPs.forEach { scanResult ->
            val rssiDistance = rssiToDistance(scanResult.level)
            Log.d("PositionLog", "RSSI 측정: AP ${scanResult.BSSID}, RSSI=${scanResult.level}dBm, 거리=${rssiDistance}m")
            
            val measurement = RangingMeasurement(
                bssid = scanResult.BSSID,
                distance = rssiDistance,
                rssi = scanResult.level,
                confidence = calculateRssiConfidence(scanResult.level)
            )
            
            measurementManager.addMeasurement(measurement)
            results.add(measurement)
        }
    } else {
        // 매칭된 AP가 없는 경우, 신호가 강한 상위 3개 사용
        val strongestAPs = scanResults
            .sortedByDescending { it.level }
            .take(3)
        
        Log.d("PositionLog", "RSSI 측정: 매칭된 AP 없음, 신호 강도 기준 상위 AP ${strongestAPs.size}개 사용")
        
        strongestAPs.forEachIndexed { index, scanResult ->
            val rssiDistance = rssiToDistance(scanResult.level)
            Log.d("PositionLog", "RSSI 측정: 강한 AP[$index] ${scanResult.BSSID}, RSSI=${scanResult.level}dBm, 거리=${rssiDistance}m")
            
            // 동적으로 AP 정보 추가 - 신호 강도에 따른 상대적 위치 배치로 개선
            val position = calculateRelativePosition(scanResult, index, strongestAPs)
            
            // SSID가 없으면 BSSID의 일부를 사용
            val apName = scanResult.SSID.ifEmpty { "AP-${scanResult.BSSID.takeLast(5)}" }
            measurementManager.updateAccessPoint(
                id = apName,
                bssid = scanResult.BSSID,
                x = position.first,
                y = position.second,
                confidence = calculateRssiConfidence(scanResult.level)
            )
            
            val measurement = RangingMeasurement(
                bssid = scanResult.BSSID,
                distance = rssiDistance,
                rssi = scanResult.level,
                confidence = calculateRssiConfidence(scanResult.level)
            )
            
            measurementManager.addMeasurement(measurement)
            results.add(measurement)
        }
    }
    
    return results
}

// RSSI 신뢰도 계산
private fun calculateRssiConfidence(rssi: Int): Double {
    return when {
        rssi >= -50 -> 0.9
        rssi >= -60 -> 0.8
        rssi >= -70 -> 0.7
        rssi >= -80 -> 0.6
        else -> 0.5
    }
}

/**
 * AP의 상대적 위치를 계산
 * 신호 강도와 주변 AP 정보를 기반으로 더 현실적인 위치 계산
 */
private fun calculateRelativePosition(
    currentAP: android.net.wifi.ScanResult,
    index: Int,
    allAPs: List<android.net.wifi.ScanResult>
): Pair<Double, Double> {
    // 가장 신호가 강한 AP를 기준점(0,0)으로 설정
    if (index == 0) {
        return Pair(0.0, 0.0)
    }
    
    // 기준 AP와의 RSSI 차이를 이용하여 상대적 거리 계산
    val strongestAP = allAPs[0]
    val rssiDiff = strongestAP.level - currentAP.level
    
    // RSSI 차이가 클수록 더 멀리 배치 (경험적 계산식)
    val relativeDistance = 5.0 + (rssiDiff * 0.2)  // 기본 5m에 RSSI 차이에 비례해 거리 증가
    
    // 주파수에 따른 각도 조정 (5GHz는 더 작은 영역 커버)
    val baseAngle = if (currentAP.frequency > 5000) {
        (index * Math.PI / 3) + (Math.PI / 6)  // 5GHz는 좁은 영역
    } else {
        (index * Math.PI / 2)  // 2.4GHz는 넓은 영역
    }
    
    // 원형으로 배치하되, RSSI에 따라 거리 조정
    return Pair(
        relativeDistance * cos(baseAngle),
        relativeDistance * sin(baseAngle)
    )
}
