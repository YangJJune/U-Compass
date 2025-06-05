package com.ikseong.ucompass.ui.room.screen

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.NaverMapComponent
import com.ikseong.ucompass.ui.common.component.ObserveAsEvents
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.room.component.RoomBottomSheet
import com.ikseong.ucompass.ui.room.component.RoomBottomSheetDragHandle
import com.ikseong.ucompass.ui.room.component.RoomDefaultContent
import com.ikseong.ucompass.ui.room.component.RoomDeleteDialog
import com.ikseong.ucompass.ui.room.component.RoomSearchContent
import com.ikseong.ucompass.ui.room.component.RoomTopComponent
import com.ikseong.ucompass.ui.room.viewmodel.ParticipantState
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiAction
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiEvent
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiState
import com.ikseong.ucompass.ui.room.viewmodel.RoomViewModel
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import com.ikseong.ucompass.ui.util.DeviceOrientationUtil
import com.ikseong.ucompass.ui.util.GpsLocationUtil.startLocationUpdates
import com.ikseong.ucompass.ui.util.GpsLocationUtil.stopLocationUpdates
import com.ikseong.ucompass.ui.util.LocationUtil.offsetLatLng
import com.ikseong.ucompass.ui.util.viewutil.noRippleClickable
import com.ikseong.ucompass.ui.util.viewutil.plus
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RoomRoute(
    id: Long,
    padding: PaddingValues,
    navigateBack: () -> Unit,
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by roomViewModel.uiState.collectAsStateWithLifecycle()
    val currentLocation by roomViewModel.currentLocation.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val deviceOrientation = DeviceOrientationUtil.rememberDeviceOrientation()

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
        roomViewModel.getRoomItem(id)
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
                            roomViewModel.onRoomUiAction(
                                RoomUiAction.OnLocationUpdate(
                                    location,
                                    deviceOrientation.value
                                )
                            )
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

    ObserveAsEvents(flow = roomViewModel.uiEvent) { event ->
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
        deviceOrientation = deviceOrientation.value,
        onAction = roomViewModel::onRoomUiAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    padding: PaddingValues,
    scaffoldState: BottomSheetScaffoldState,
    uiState: RoomUiState,
    currentLocation: LatLng?,
    deviceOrientation: Double,
    onAction: (RoomUiAction) -> Unit,
) {
    // 카메라 상태 기억
    val cameraPositionState = rememberCameraPositionState()

    // 기기 방향이 변경될 때마다 지도 회전 업데이트
    LaunchedEffect(
        uiState.isMapVisible,
        currentLocation,
        deviceOrientation
    ) {
        currentLocation?.let { location ->
            // 기기 방향 각도의 반대 방향으로 지도 회전 (기기가 시계방향으로 회전하면 지도는 반시계방향으로)
            val offsetLatLng = offsetLatLng(
                location,
                -90.0, // 90m 위쪽으로 이동 = 내 위치를 아래에 보이게
                (deviceOrientation + 180) % 360 // 반대방향 bearing
            )

            cameraPositionState.position = CameraPosition(
                offsetLatLng,
                17.0,
                0.0,
                deviceOrientation.toDouble()
            )
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetDragHandle = { RoomBottomSheetDragHandle() },
        sheetContent = {
            RoomBottomSheet(
//                modifier = Modifier.padding(padding),
                participantInfo = uiState.participantState,
                onUserClick = { onAction(RoomUiAction.OnUserShownClick(it)) },
                onAllClick = { onAction(RoomUiAction.OnAllUserShownClick) }
            )
        }
    ) { additionalPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding + additionalPadding)
        ) {
            // 먼저 지도를 렌더링 (가장 낮은 z-index)
            if (uiState.isSearchMode) {
                // 네이버 지도 표시
                currentLocation?.let { location ->
                    // 참가자 정보를 MapMarker로 변환
                    onAction(RoomUiAction.OnLocationUpdate(location, deviceOrientation))

                    NaverMapComponent(
                        markers = uiState.mapMarkers,
                        currentLocation = location,
                        cameraPositionState = cameraPositionState,
                        isMapVisible = uiState.isMapVisible,
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
                        onSearchClick = { onAction(RoomUiAction.OnLottieClick(it)) }
                    )
                } else {
                    RoomSearchContent(
                        address = uiState.address,
                        isMapVisible = uiState.isMapVisible,
                        myLocation = currentLocation,
                        mapMarkers = uiState.mapMarkers,
                    )
                }
            }
            if (!uiState.isSearchMode) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp)
                        .width(194.dp)
                        .height(72.dp)
                        .border(
                            width = 2.dp,
                            shape = RoundedCornerShape(25.dp),
                            color = Color(0xFF00E397)
                        )
                        .noRippleClickable { onAction(RoomUiAction.OnUserListClick) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_room_participant_40),
                        contentDescription = null,
                        tint = Color(0x8000E397)
                    )
                    Text(
                        text = "참가 인원 ${uiState.participantCount}명",
                        style = typography.medium.copy(
                            fontSize = 18.sp,
                            color = Color(0xFF606060)
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .padding(bottom = 21.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White)
                        .padding(horizontal = 6.5.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_map_toggle),
                        contentDescription = "Map Toggle",
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable {
                            onAction(
                                RoomUiAction.OnMapToggleClick(
                                    uiState.isMapVisible
                                )
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .height(64.dp)
                            .noRippleClickable { onAction(RoomUiAction.OnUserListClick) }
                            .clip(RoundedCornerShape(25.dp))
                            .background(
                                if (uiState.isMapVisible) Color(0xFFD8FCF0)
                                else Color.White
                            )
                            .padding(start = 20.dp, end = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_room_participant_40),
                            contentDescription = null,
                            tint = Color(0x8000E397)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "참가 인원 ${uiState.participantCount}명",
                            style = typography.medium.copy(
                                fontSize = 18.sp,
                                color = Color(0xFF606060)
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color(0xFFD9D9D9))
                            .size(64.dp)
                            .clickable { onAction(RoomUiAction.OnDeleteClick) }
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painterResource(id = R.drawable.ic_room_delete_36),
                            contentDescription = "Map Toggle",
                            tint = Color.Unspecified,
                        )
                    }
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
            participantState = listOf(
                ParticipantState(
                    name = "John Doe",
                    profileUrl = "https://example.com/profile.jpg",
                    direction = Direction.E,
                    distance = 1000,
                    isShown = true,
                ),
                ParticipantState(
                    name = "Jane Smith",
                    profileUrl = "https://example.com/profile2.jpg",
                    direction = Direction.N,
                    distance = 1500,
                    isShown = false,
                ),
                ParticipantState(
                    name = "Alice Johnson",
                    profileUrl = "https://example.com/profile3.jpg",
                    direction = Direction.SE,
                    distance = 2000,
                    isShown = true,
                )
            ),
        ),
        currentLocation = LatLng(37.5666805, 126.9784147),
        onAction = {},
        deviceOrientation = 0.0 // 임시 값, 실제로는 기기 방향에 따라 변경되어야 함
    )
}