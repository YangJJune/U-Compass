package com.example.composesensor.locate

import android.Manifest
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationRoute(
    padding: PaddingValues
) {
    val context = LocalContext.current
    // FusedLocationProviderClient 인스턴스 준비
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var errorMassage by remember { mutableStateOf("") }

    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(key1 = locationPermissionState.status.isGranted) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else if (locationPermissionState.status.isGranted) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc ->
                        currentLocation = loc
                    }
                    .addOnFailureListener { e ->
                        errorMassage = "위치 정보를 가져오지 못했습니다: ${e.message}"
                    }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    currentLocation?.let { loc ->
        val targetLatitude = 37.5665
        val targetLongitude = 126.9780

// Location 객체를 이용한 거리 및 방위 계산
        val targetLocation = Location("").apply {
            latitude = targetLatitude
            longitude = targetLongitude
        }
        val distanceMeters = loc.distanceTo(targetLocation)       // 현재 위치와 목표 위치 간 거리 (미터)
        val initialBearing = loc.bearingTo(targetLocation)       // -180~180도 범위의 초기 방위각
        val bearingToTarget =
            if (initialBearing < 0) initialBearing + 360 else initialBearing  // 0~360도 보정

        Text(
            text = "목표까지 거리: ${"%.0f".format(distanceMeters)}m, 방위각: ${"%.1f".format(bearingToTarget)}°"
        )
    }
}

@Composable
fun LocationScreen(
    padding: PaddingValues,
    currentLocation: Location,
    distanceMeters: Float,
    bearingToTarget: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Text(text = "현재 위치: ${currentLocation.latitude}, ${currentLocation.longitude}")
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(Color.Gray)
        )
        Text(
            text = "목표까지 거리: ${"%.0f".format(distanceMeters)}m, 방위각: ${"%.1f".format(bearingToTarget)}°"
        )
        ArrowDirectionIndicator(
            modifier = Modifier
                .padding(top = 20.dp),
            bearingToTarget = bearingToTarget
        )
    }
}
