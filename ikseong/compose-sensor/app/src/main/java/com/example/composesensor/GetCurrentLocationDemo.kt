package com.example.composesensor

import android.Manifest
import android.location.Location
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetCurrentLocationDemo() {
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
        Text(text = "현재 위치: ${loc.latitude}, ${loc.longitude}")
        
    }

}
