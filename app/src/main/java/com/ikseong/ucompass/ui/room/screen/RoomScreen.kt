package com.ikseong.ucompass.ui.room.screen

import android.Manifest
import android.os.Build
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ikseong.ucompass.ui.util.DeviceOrientationUtil
import com.ikseong.ucompass.ui.util.GpsLocationUtil.startLocationUpdates
import com.ikseong.ucompass.ui.util.GpsLocationUtil.stopLocationUpdates
import com.ikseong.ucompass.ui.util.viewutil.plus
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch



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

    LaunchedEffect(Unit) {
        viewModel.getRoomItem(id)
    }

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

    // 기기 방향 각도 관찰 (0~360도)
    val deviceOrientation = DeviceOrientationUtil.rememberDeviceOrientation()

    // 기기 방향이 변경될 때마다 지도 회전 업데이트
    LaunchedEffect(
        uiState.isMapVisible,
        currentLocation,
        deviceOrientation.value
    ) {
        if (uiState.isMapVisible && currentLocation != null) {
            // 기기 방향 각도의 반대 방향으로 지도 회전 (기기가 시계방향으로 회전하면 지도는 반시계방향으로)
            val mapBearing = deviceOrientation.value
            val cameraLocation = LatLng(
                currentLocation.latitude + 0.00083,
                currentLocation.longitude
            )
            // 현재 카메라 위치 유지하면서 베어링(회전)만 업데이트
            cameraPositionState.position = CameraPosition(
                cameraLocation,
                17.0,
                0.0,
//                cameraPositionState.position.tilt,
                mapBearing.toDouble()
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
                            isCompassEnabled = true,// 나침반 활성화
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