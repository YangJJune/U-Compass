package com.example.composesensor

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission() {
    val context = LocalContext.current
    val locationPermissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = locationPermissions
    )
    var showRationale by remember { mutableStateOf(false) }
    val backgroundPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    val backgroundPermissionState = rememberPermissionState(backgroundPermission)

    when {
        multiplePermissionsState.allPermissionsGranted -> {
            Text("위치 권한이 허용되었습니다!")
            if (!backgroundPermissionState.status.isGranted) {

                Text("앱 설정에서 백그라운드 권한을 허용해주세요.")
                Button(onClick = {
                    backgroundPermissionState.launchPermissionRequest()
                }) {
                    Text("설정으로 이동")
                }
            }
        }
        multiplePermissionsState.shouldShowRationale -> {
            Text("위치 기능을 사용하기 위해서는 위치 권한이 필요합니다.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }) {
                Text("권한 허용하기")
            }
        }
        else -> {
            if (showRationale) {
                Text("[2회 거절] 권한이 필요합니다. 앱 설정에서 권한을 허용해주세요.")
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("설정으로 이동")
                }
            } else {
                Text("최초 요청")
                Button(onClick = {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                    showRationale = true
                }) {
                    Text("권한 요청하기")
                }
            }
        }
    }
}
