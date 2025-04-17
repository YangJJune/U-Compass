package com.example.composesensor.locate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// INS 내비게이션 화면의 진입점 컴포저블
@Composable
fun InsNavigationRoute(
    padding: PaddingValues
) {
    InsNavigationScreen(padding = padding)
}

// 메인 INS 내비게이션 화면
@SuppressLint("DefaultLocale")
@Composable
fun InsNavigationScreen(
    padding: PaddingValues
) {
    val context = LocalContext.current
    
    // 권한 관련 상태
    val requiredPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }
    
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
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED)
        }.toMap()
        
        allPermissionsGranted = permissionResults.all { it.value }
        
        if (!allPermissionsGranted) {
            multiplePermissionLauncher.launch(requiredPermissions)
        }
    }
    
    // INS 센서 관련 상태
    val sensorManager = remember { 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager 
    }
    
    // 센서 객체
    val accelerometer = remember { 
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) 
    }
    val magnetometer = remember { 
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) 
    }
    
    // 센서 데이터 상태
    var accelValues by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }
    var magnetValues by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }
    
    // 위치 및 방향 관련 상태
    var initialLocation by remember { mutableStateOf<Location?>(null) }
    var currentLatitude by remember { mutableDoubleStateOf(0.0) }
    var currentLongitude by remember { mutableDoubleStateOf(0.0) }
    var currentAzimuth by remember { mutableFloatStateOf(0f) }
    var lastUpdateTime by remember { mutableLongStateOf(0L) }
    
    // INS 사용 여부
    var insActive by remember { mutableStateOf(false) }
    
    // 보정 관련 상태
    var calibrationMode by remember { mutableStateOf(false) }
    var stepLength by remember { mutableFloatStateOf(0.75f) } // 기본 보폭: 0.75m
    var stepCount by remember { mutableIntStateOf(0) }
    
    // 로그 메시지
    var logMessages by remember { mutableStateOf(listOf<String>()) }
    
    // 위치 제공자
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    // 위치 업데이트 콜백
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (initialLocation == null) {
                        initialLocation = location
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude
                        logMessages = addLogMessage(logMessages) { "초기 위치 설정: 위도 ${location.latitude}, 경도 ${location.longitude}" }
                    } else if (!insActive) {
                        // INS가 활성화되지 않은 경우에만 GPS 위치 업데이트
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude
                    }
                    break
                }
            }
        }
    }
    
    // 위치 권한이 있는 경우 위치 업데이트 요청
    LaunchedEffect(allPermissionsGranted) {
        if (allPermissionsGranted) {
            try {
                val locationRequest = LocationRequest.Builder(1000) // 1초마다 업데이트
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()
                
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                
                // 마지막 알려진 위치 가져오기
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        initialLocation = location
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude
                        logMessages = addLogMessage(logMessages) { "초기 위치 설정: 위도 ${location.latitude}, 경도 ${location.longitude}" }
                    }
                }
            } catch (e: SecurityException) {
                logMessages = addLogMessage(logMessages) { "위치 권한 오류: ${e.message}" }
            }
        }
    }
    
    // 센서 리스너 등록 및 해제
    DisposableEffect(Unit) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelValues = event.values.clone()
                        detectStep(accelValues, lastUpdateTime) { isStep ->
                            if (isStep && insActive) {
                                stepCount++
                                updatePositionWithINS(
                                    currentAzimuth,
                                    stepLength,
                                    currentLatitude,
                                    currentLongitude
                                ) { lat, lng ->
                                    currentLatitude = lat
                                    currentLongitude = lng
                                }
                            }
                            lastUpdateTime = System.currentTimeMillis()
                        }
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        magnetValues = event.values.clone()
                        // 방위각 계산
                        calculateOrientation(accelValues, magnetValues) { azimuth ->
                            currentAzimuth = azimuth
                        }
                    }
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // 정확도 변경 처리 (필요시 구현)
            }
        }
        
        // 센서 리스너 등록
        sensorManager.registerListener(
            sensorListener, 
            accelerometer, 
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener, 
            magnetometer, 
            SensorManager.SENSOR_DELAY_NORMAL
        )
        
        // 컴포저블이 사라질 때 리스너 해제
        onDispose {
            sensorManager.unregisterListener(sensorListener)
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    
    // UI 부분
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 제목 및 설명
            Text(
                text = "INS 내비게이션",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "관성 항법 시스템을 이용한 실내 위치 추적",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 네이버 지도 표시
            MyNaverMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                latLng = LatLng(currentLatitude, currentLongitude),
                initialZoom = 15.0,
                autoMoveCamera = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 위치 정보 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "위치 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LocationInfoItem(label = "위도", value = currentLatitude.toString())
                    LocationInfoItem(label = "경도", value = currentLongitude.toString())
                    LocationInfoItem(label = "방향", value = "${currentAzimuth.toInt()}°")
                    
                    if (insActive) {
                        LocationInfoItem(label = "걸음 수", value = stepCount.toString())
                        LocationInfoItem(label = "보폭 길이", value = "${stepLength}m")
                    }
                }
            }
            
            // INS 제어 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INS 제어",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // INS 활성화/비활성화 스위치
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INS 내비게이션 사용",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = insActive,
                            onCheckedChange = { checked ->
                                insActive = checked
                                if (checked) {
                                    logMessages = addLogMessage(logMessages) { "INS 내비게이션 활성화" }
                                } else {
                                    logMessages = addLogMessage(logMessages) { "INS 내비게이션 비활성화" }
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 초기화 버튼
                    Button(
                        onClick = {
                            // 현재 GPS 위치로 초기화
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                if (location != null) {
                                    initialLocation = location
                                    currentLatitude = location.latitude
                                    currentLongitude = location.longitude
                                    stepCount = 0
                                    logMessages = addLogMessage(logMessages) { "위치 초기화: 위도 ${location.latitude}, 경도 ${location.longitude}" }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("현재 GPS 위치로 초기화")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 보폭 설정
                    Text(
                        text = "보폭 설정: ${stepLength}m",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Slider(
                        value = stepLength,
                        onValueChange = { stepLength = it },
                        valueRange = 0.3f..1.2f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // 센서 데이터 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "센서 데이터",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 가속도계 데이터
                    Text(
                        text = "가속도계 (m/s²):",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "X: ${String.format("%.2f", accelValues[0])}, " +
                                "Y: ${String.format("%.2f", accelValues[1])}, " +
                                "Z: ${String.format("%.2f", accelValues[2])}",
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 자기장 센서 데이터
                    Text(
                        text = "자기장 센서 (μT):",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "X: ${String.format("%.2f", magnetValues[0])}, " +
                                "Y: ${String.format("%.2f", magnetValues[1])}, " +
                                "Z: ${String.format("%.2f", magnetValues[2])}",
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            
            // 로그 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "로그",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        IconButton(onClick = { logMessages = emptyList() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "로그 초기화"
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 로그 메시지 표시
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        Column {
                            if (logMessages.isEmpty()) {
                                Text(
                                    text = "로그 메시지가 없습니다",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            } else {
                                logMessages.reversed().take(10).forEach { message ->
                                    Text(
                                        text = message,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 방위각 계산 함수
private fun calculateOrientation(
    accelValues: FloatArray,
    magnetValues: FloatArray,
    onResult: (Float) -> Unit
) {
    val rotationMatrix = FloatArray(9)
    val orientationAngles = FloatArray(3)
    
    val success = SensorManager.getRotationMatrix(
        rotationMatrix,
        null,
        accelValues,
        magnetValues
    )
    
    if (success) {
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        // orientationAngles[0]는 방위각(라디안)
        val azimuthRad = orientationAngles[0]
        var azimuthDegrees = Math.toDegrees(azimuthRad.toDouble()).toFloat()
        
        // 음수 값을 0~360도로 변환
        if (azimuthDegrees < 0) {
            azimuthDegrees += 360f
        }
        
        onResult(azimuthDegrees)
    }
}

// 걸음 감지 함수
private fun detectStep(
    accelValues: FloatArray,
    lastUpdateTime: Long,
    onStep: (Boolean) -> Unit
) {
    val now = System.currentTimeMillis()
    
    // 가속도 크기 계산
    val magnitude = sqrt(
        accelValues[0].toDouble().pow(2) +
        accelValues[1].toDouble().pow(2) +
        accelValues[2].toDouble().pow(2)
    ).toFloat()
    
    // 임계값 및 쿨다운 시간 설정
    val threshold = 11.5f  // 걸음 감지 임계값 (중력 가속도 포함)
    val cooldownMs = 400   // 최소 걸음 간격 (ms)
    
    // 걸음 감지 로직
    val timeDiff = now - lastUpdateTime
    if (magnitude > threshold && timeDiff > cooldownMs) {
        onStep(true)
    } else {
        onStep(false)
    }
}

// INS 기반 위치 업데이트 함수
private fun updatePositionWithINS(
    azimuth: Float,
    stepLength: Float,
    currentLat: Double,
    currentLng: Double,
    onUpdate: (Double, Double) -> Unit
) {
    // 방위각을 라디안으로 변환
    val azimuthRad = Math.toRadians(azimuth.toDouble())
    
    // 북쪽 및 동쪽 방향으로의 이동량 (미터)
    val northMeter = stepLength * cos(azimuthRad)
    val eastMeter = stepLength * sin(azimuthRad)
    
    // 지구 반경 (미터)
    val earthRadius = 6378137.0
    
    // 위도 변화량 (라디안)
    val latRad = Math.toRadians(currentLat)
    val latChange = northMeter / earthRadius
    
    // 경도 변화량 (라디안)
    val lngChange = eastMeter / (earthRadius * cos(latRad))
    
    // 라디안에서 도(degree)로 변환
    val newLat = currentLat + Math.toDegrees(latChange)
    val newLng = currentLng + Math.toDegrees(lngChange)
    
    onUpdate(newLat, newLng)
}

// 로그 메시지 추가
private fun addLogMessage(
    currentMessages: List<String>,
    message: () -> String
): List<String> {
    val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    val newMessage = "[$timestamp] ${message()}"
    return currentMessages + newMessage
} 