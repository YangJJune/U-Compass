package com.ikseong.ucompass.ui.room.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ikseong.ucompass.ui.common.component.MapMarker
import com.ikseong.ucompass.ui.common.component.NaverMapComponent
import com.ikseong.ucompass.ui.common.component.ObserveAsEvents
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.model.ParticipantInfo
import com.ikseong.ucompass.ui.room.component.RoomBottomSheet
import com.ikseong.ucompass.ui.room.component.RoomBottomSheetDragHandle
import com.ikseong.ucompass.ui.room.component.RoomDefaultContent
import com.ikseong.ucompass.ui.room.component.RoomDeleteDialog
import com.ikseong.ucompass.ui.room.component.RoomSearchContent
import com.ikseong.ucompass.ui.room.component.RoomTopComponent
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiAction
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiEvent
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiState
import com.ikseong.ucompass.ui.room.viewmodel.RoomViewModel
import com.ikseong.ucompass.ui.util.GpsLocationUtil
import com.ikseong.ucompass.ui.util.WifiRttUtil
import com.ikseong.ucompass.ui.util.viewutil.plus
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val TAG = "RoomScreen"
private const val LOCATION_UPDATE_INTERVAL = 10000L // 10초

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RoomRoute(
    id: Long,
    padding: PaddingValues,
    navigateBack: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 필요한 모든 권한 상태 관리
    val permissionsState = rememberMultiplePermissionsState(
        permissions = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_WIFI_STATE)
            add(Manifest.permission.CHANGE_WIFI_STATE)

            // Android 13 (API 33) 이상에서 필요한 권한
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.NEARBY_WIFI_DEVICES)
            }
        }
    )

    // 앱 생명주기 관찰하여 위치 업데이트 관리
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (permissionsState.allPermissionsGranted) {
                        // 위치 업데이트 시작
                        startLocationUpdates(context) { location ->
                            viewModel.onRoomUiAction(RoomUiAction.OnLocationUpdate(location))
                        }
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    // 위치 업데이트 중지
                    stopLocationUpdates(context)
                }

                else -> { /* no-op */
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stopLocationUpdates(context)
        }
    }

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            RoomUiEvent.NavigateToBack -> navigateBack()
            RoomUiEvent.ShowBottomSheet -> scope.launch { scaffoldState.bottomSheetState.expand() }
        }
    }

    RoomScreen(
        padding = padding,
        uiState = uiState,
        currentLocation = currentLocation,
        scaffoldState = scaffoldState,
        onAction = viewModel::onRoomUiAction
    )
}

// 위치 업데이트 시작 - WiFi RTT와 GPS 모두 시작
private fun startLocationUpdates(context: Context, onLocationUpdate: (LatLng) -> Unit) {
    // 마지막으로 사용된 위치 소스를 추적
    var lastUsedSource = "초기화"

    // WiFi RTT 시작 (10초마다 체크하고, 지원될 때만 위치 계산)
    WifiRttUtil.startRttUpdateTimer(context, LOCATION_UPDATE_INTERVAL) { rttLocation ->
        // RTT로 위치를 얻을 수 있다면 사용, 아니면 무시
        rttLocation?.let {
            Log.d(TAG, "WiFi RTT로 측정된 위치 사용: $it")

            // 위치 소스가 변경되었거나 처음 사용되는 경우에만 토스트 표시
            if (lastUsedSource != "WiFi RTT") {
                lastUsedSource = "WiFi RTT"

                // WiFi RTT 사용 시 토스트 메시지 표시
                val rttAccessPoints = WifiRttUtil.getLastRttAccessPointCount()
                val message = "WiFi RTT 위치 측정 중\n" +
                        "- 측정된 AP 개수: $rttAccessPoints\n" +
                        "- 위치: ${it.latitude.format(5)}, ${it.longitude.format(5)}"

                showToast(context, message)
            }

            onLocationUpdate(it)
        }
    }

    // GPS 위치 측정 시작 (기본 위치 소스로 사용)
    GpsLocationUtil.startLocationUpdates(context, LOCATION_UPDATE_INTERVAL) { gpsLocation ->
        Log.d(TAG, "GPS 위치 사용: $gpsLocation")

        // 위치 소스가 변경되었거나 처음 사용되는 경우에만 토스트 표시
        if (lastUsedSource != "GPS") {
            lastUsedSource = "GPS"

            // GPS 사용 시 토스트 메시지 표시
            val accuracy = GpsLocationUtil.getLastLocationAccuracy()
            val message = "GPS 위치 측정 중\n" +
                    "- 정확도: ${accuracy}m\n" +
                    "- 위치: ${gpsLocation.latitude.format(5)}, ${gpsLocation.longitude.format(5)}"

            showToast(context, message)
        }

        onLocationUpdate(gpsLocation)
    }
}

// 위치 업데이트 중지 - 모든 소스 중지
private fun stopLocationUpdates(context: Context) {
    WifiRttUtil.stopRttUpdateTimer()
    GpsLocationUtil.stopLocationUpdates(context)
}

// 토스트 메시지 표시 함수
private fun showToast(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

// Double 값을 지정된 소수점 자릿수로 포맷팅하는 확장 함수
private fun Double.format(digits: Int): String = String.format("%.${digits}f", this)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    padding: PaddingValues,
    scaffoldState: BottomSheetScaffoldState,
    uiState: RoomUiState,
    currentLocation: LatLng?,
    onAction: (RoomUiAction) -> Unit,
) {
    // 카메라 상태 기억
    val cameraPositionState = rememberCameraPositionState()

    // 지도가 보이게 될 때만 카메라 위치를 업데이트
    LaunchedEffect(uiState.isMapVisible, currentLocation) {
        // 지도가 보이게 되었을 때 && 현재 위치가 있을 때만 카메라 위치 업데이트
        if (uiState.isMapVisible && currentLocation != null) {
            // 초기 카메라 위치를 현재 위치로 설정 (줌 레벨 17)
            cameraPositionState.position = CameraPosition(
                currentLocation, 17.0
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetDragHandle = { RoomBottomSheetDragHandle() },
        sheetContent = {
            RoomBottomSheet(
                participantInfo = uiState.participantInfo,
                onUserClick = { onAction(RoomUiAction.OnUserShownClick(it)) },
                onAllClick = { onAction(RoomUiAction.OnAllUserShownClick) }
            )
        }
    ) { additionalPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (uiState.isSearchMode) Color(0xFF090A38) else Color.White
                )
                .padding(padding + additionalPadding)
        ) {
            // 먼저 지도를 렌더링 (가장 낮은 z-index)
            if (uiState.isSearchMode && uiState.isMapVisible) {
                // 네이버 지도 표시
                currentLocation?.let { location ->
                    // 참가자 정보를 MapMarker로 변환
                    val mapMarkers = uiState.participantInfo
                        .filter { it.isShown }
                        .map { participant ->
                            MapMarker(
                                id = participant.name,
                                name = participant.name,
                                latitude = participant.latitude,
                                longitude = participant.longitude,
                                isVisible = participant.isShown,
                                distanceText = "${participant.distance}m"
                            )
                        }.toImmutableList()

                    NaverMapComponent(
                        markers = mapMarkers,
                        currentLocation = location,
                        cameraPositionState = cameraPositionState,
                        onMapClick = { /* 지도 클릭 이벤트 무시 */ },
                        uiSettings = MapUiSettings(
                            // 모든 제스처 비활성화
                            isScrollGesturesEnabled = false, // 스크롤 제스처 비활성화 (지도 이동 불가)
                            isZoomGesturesEnabled = false, // 줌 제스처 비활성화 (핀치 줌 불가)
                            isRotateGesturesEnabled = false,// 회전 제스처 비활성화
                            isTiltGesturesEnabled = false,// 틸트 제스처 비활성화
                            isStopGesturesEnabled = false,// 애니메이션 중 탭으로 중지 불가

                            // UI 컨트롤 비활성화
                            isCompassEnabled = false,// 나침반 비활성화
                            isScaleBarEnabled = true,// 축척 바는 유지 (거리감 제공)
                            isZoomControlEnabled = false,// 줌 컨트롤 비활성화
                            isIndoorLevelPickerEnabled = false, // 실내지도 층 피커 비활성화
                            isLocationButtonEnabled = false,// 현위치 버튼 비활성화
                        )
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RoomTopComponent(
                    onBackClick = { onAction(RoomUiAction.OnBackClick) },
                    onDeleteClick = { onAction(RoomUiAction.OnDeleteClick) },
                    isSearchMode = uiState.isSearchMode,
                    isMapVisible = uiState.isMapVisible,
                    roomName = uiState.roomName
                )
                if (!uiState.isSearchMode) {
                    RoomDefaultContent(
                        address = uiState.address,
                        participantCount = uiState.participantInfo.size,
                        onSearchClick = { onAction(RoomUiAction.OnLottieClick(it)) }
                    )
                } else {
                    RoomSearchContent(
                        address = uiState.address,
                        participantCount = uiState.participantInfo.size,
                        isMapVisible = uiState.isMapVisible,
                        onMapToggleClick = { flag ->
                            onAction(RoomUiAction.OnMapToggleClick(flag))
                        },
                        onDeleteClick = { onAction(RoomUiAction.OnDeleteClick) },
                        onUserListClick = { onAction(RoomUiAction.OnUserListClick) },
                    )
                }
            }
        }


        if (uiState.isRoomDeleteDialogVisible) {
            RoomDeleteDialog(
                isHost = uiState.isHost,
                onDismissRequest = { onAction(RoomUiAction.OnDeleteCancelClick) },
                onCancelClick = { onAction(RoomUiAction.OnDeleteCancelClick) },
                onConfirmClick = { onAction(RoomUiAction.OnDeleteConfirmClick) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun RoomScreenPreview() {
    RoomScreen(
        padding = PaddingValues(0.dp),
        scaffoldState = rememberBottomSheetScaffoldState(),
        uiState = RoomUiState(
            roomName = "Room Name",
            address = "123 Main St, City, Country",
            participantInfo = listOf(
                ParticipantInfo(
                    name = "John Doe",
                    profileUrl = "https://example.com/profile.jpg",
                    direction = Direction.E,
                    distance = 1000,
                    isShown = true,
                ),
                ParticipantInfo(
                    name = "Jane Smith",
                    profileUrl = "https://example.com/profile2.jpg",
                    direction = Direction.N,
                    distance = 1500,
                    isShown = false,
                ),
                ParticipantInfo(
                    name = "Alice Johnson",
                    profileUrl = "https://example.com/profile3.jpg",
                    direction = Direction.SE,
                    distance = 2000,
                    isShown = true,
                )
            ),
        ),
        currentLocation = LatLng(37.5666805, 126.9784147),
        onAction = {}
    )
}