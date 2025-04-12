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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationRoute(
    padding: PaddingValues
) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var errorMassage by remember { mutableStateOf("") }
    var lastUpdateTime by remember { mutableLongStateOf(0L) }
    var updateDuration by remember { mutableLongStateOf(0L) }

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
                        lastUpdateTime = System.currentTimeMillis()
                    }
                    .addOnFailureListener { e ->
                        errorMassage = "위치 정보를 가져오지 못했습니다: ${e.message}"
                    }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val now = System.currentTimeMillis()
                    if (lastUpdateTime > 0) {
                        updateDuration = now - lastUpdateTime
                    }
                    lastUpdateTime = now
                    currentLocation = location
                }
            }
        }
    }

    DisposableEffect(key1 = locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            val locationRequest = LocationRequest.Builder(1000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(1f)
                .build()

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    var distanceMeters by remember { mutableFloatStateOf(0f) }
    var initialBearing by remember { mutableFloatStateOf(0f) }
    var bearingToTarget by remember { mutableFloatStateOf(0f) }

    currentLocation?.let { loc ->
        val targetLatitude = 37.5665
        val targetLongitude = 126.9780

        val targetLocation = Location("").apply {
            latitude = targetLatitude
            longitude = targetLongitude
        }
        distanceMeters = loc.distanceTo(targetLocation)
        initialBearing = loc.bearingTo(targetLocation)
        bearingToTarget = if (initialBearing < 0) initialBearing + 360 else initialBearing

        LocationScreen(
            padding = padding,
            currentLocation = loc,
            distanceMeters = distanceMeters,
            bearingToTarget = bearingToTarget,
            updateDuration = updateDuration
        )
    }
}

@Composable
fun LocationScreen(
    padding: PaddingValues,
    currentLocation: Location,
    distanceMeters: Float,
    bearingToTarget: Float,
    updateDuration: Long
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
        Text(
            text = "위치 업데이트 소요 시간: ${updateDuration}ms",
            modifier = Modifier.padding(vertical = 4.dp)
        )
        ArrowDirectionIndicator(
            modifier = Modifier
                .padding(top = 20.dp),
            bearingToTarget = bearingToTarget
        )
    }
}
