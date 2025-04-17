package com.example.composesensor.locate

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NaverMapRoute(
    padding: PaddingValues
) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    // 로그 추가 - 권한 상태 확인
    LaunchedEffect(key1 = Unit) {
        Log.d("LocationPermission", "Permission status: ${locationPermissionState.status}")
        Log.d("LocationPermission", "Is granted: ${locationPermissionState.status.isGranted}")
    }

    // 위치 업데이트 콜백
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    currentLocation = location
                    break
                }
            }
        }
    }

    LaunchedEffect(key1 = locationPermissionState.status.isGranted) {
        if (!locationPermissionState.status.isGranted) {
            Log.d("LocationPermission", "Permission not granted, requesting permission")
            locationPermissionState.launchPermissionRequest()
        } else {
            Log.d("LocationPermission", "Permission granted, requesting location updates")
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            currentLocation = loc
                            Log.d("LocationPermission", "Last location received: $loc")
                        } else {
                            Log.d("LocationPermission", "Last location is null")
                        }
                    }
                    .addOnFailureListener { e ->
                        errorMessage = "위치 정보를 가져오지 못했습니다: ${e.message}"
                        Log.e("LocationPermission", "Failed to get location: ${e.message}", e)
                    }

                // 지속적인 위치 업데이트 요청
                val locationRequest = LocationRequest.Builder(1000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                )
                Log.d("LocationPermission", "Location updates requested")
            } catch (e: SecurityException) {
                Log.e("LocationPermission", "Security exception: ${e.message}", e)
                errorMessage = "권한 오류: ${e.message}"
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("LocationPermission", "Unexpected error: ${e.message}", e)
                errorMessage = "예상치 못한 오류: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    // 컴포넌트가 해제될 때 위치 업데이트 중지
    DisposableEffect(key1 = Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    LocationDetailScreen(
        padding = padding,
        currentLocation = currentLocation,
        errorMessage = errorMessage
    )
}

@Composable
fun LocationInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun LocationDetailScreen(
    padding: PaddingValues,
    currentLocation: Location?,
    errorMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
    ) {
        // 위치 정보 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "GPS 위치 정보",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 현재 위치 정보 표시
                currentLocation?.let { loc ->
                    LocationInfoItem("위도", "${loc.latitude}")
                    LocationInfoItem("경도", "${loc.longitude}")
                    if (loc.hasAltitude()) {
                        LocationInfoItem("고도", "${loc.altitude}m")
                    }
                    LocationInfoItem("정확도", "${loc.accuracy}m")
                    if (loc.hasSpeed()) {
                        LocationInfoItem("속도", "${loc.speed}m/s")
                    }
                    if (loc.hasBearing()) {
                        LocationInfoItem("방향", "${loc.bearing}°")
                    }
                } ?: Text(
                    text = if (errorMessage.isEmpty()) "위치 정보를 가져오는 중..." else errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        MyNaverMap(
            modifier = Modifier.padding(top = 16.dp),
            latLng = currentLocation?.let { LatLng(it.latitude, it.longitude) }
        )


        // 네이버 지도 대신 위치 정보를 시각적으로 표시
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2D91FF)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                currentLocation?.let { loc ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "현재 위치",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = "위도: ${loc.latitude}\n" +
                                    "경도: ${loc.longitude}",
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "네이버 지도 연동에 문제가 있습니다.\n" +
                                    "현재 위치 정보는 정상적으로 가져오고 있습니다.",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                } ?: Text(
                    text = "위치 정보를 가져오는 중...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MyNaverMap(
    modifier: Modifier = Modifier,
    latLng: LatLng?,
    initialZoom: Double = 11.0,
    autoMoveCamera: Boolean = false
) {
    var mapProperties by remember {
        mutableStateOf(
            MapProperties()
        )
    }
    var mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(isLocationButtonEnabled = false)
        )
    }
    val seoul = LatLng(37.532600, 127.024612)
    val myLocationLatLng = latLng ?: seoul
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        // 카메라 초기 위치를 설정합니다.
        position = CameraPosition(myLocationLatLng, initialZoom)
    }
    
    // 위치가 변경될 때 카메라 이동
    LaunchedEffect(latLng) {
        if (autoMoveCamera && latLng != null) {
            cameraPositionState.move(CameraUpdate.scrollTo(latLng))
        }
    }

    Box(
        Modifier
            .fillMaxWidth()
            .zIndex(2f)
            .height(800.dp)
    ) {
        NaverMap(
            properties = mapProperties, uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = myLocationLatLng),
                captionText = "현재 위치"
            )
            Marker(
                state = MarkerState(position = LatLng(37.5665, 126.9780)),
                captionText = "서울"
            )
//            Marker(
//                state = MarkerState(position = seoul),
//                captionText = "서울"
//            )
        }
//        Column {
//            Button(onClick = {
//                mapProperties = mapProperties.copy(
//                    isBuildingLayerGroupEnabled = !mapProperties.isBuildingLayerGroupEnabled
//                )
//            }) {
//                Text(text = "Toggle isBuildingLayerGroupEnabled")
//            }
//            Button(onClick = {
//                mapUiSettings = mapUiSettings.copy(
//                    isLocationButtonEnabled = !mapUiSettings.isLocationButtonEnabled
//                )
//            }) {
//                Text(text = "Toggle isLocationButtonEnabled")
//            }
//            Button(onClick = {
//                // 카메라를 새로운 줌 레벨로 이동합니다.
//                cameraPositionState.move(CameraUpdate.zoomIn())
//            }) {
//                Text(text = "Zoom In")
//            }
//        }
    }
}