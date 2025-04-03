package com.ikseong.composepermission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionBox(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }

    // 위치 권한 목록 정의
    val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // 여러 권한을 한번에 처리하기 위한 상태 관리
    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = locationPermissions
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            // 모든 권한이 허용된 경우
            multiplePermissionsState.allPermissionsGranted -> {
                Text("위치 권한이 허용되었습니다!")
            }
            // 권한이 거부된 경우 사용자에게 이유 설명
            multiplePermissionsState.shouldShowRationale -> {
                Text("[1회 거절] 위치 기능을 사용하기 위해서는 위치 권한이 필요합니다.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }) {
                    Text("권한 허용하기")
                }
            }
            // 처음 권한 요청하는 경우
            else -> {
                if (showRationale) {
                    // 2회 이상 거부한 경우 설정으로 이동
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
                    // 최초 요청
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
}

@Preview
@Composable
private fun LocationPermissionBoxPreview() {
    LocationPermissionBox()
}