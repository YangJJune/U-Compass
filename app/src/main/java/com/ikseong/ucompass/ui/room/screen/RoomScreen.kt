package com.ikseong.ucompass.ui.room.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
import com.ikseong.ucompass.ui.util.viewutil.plus
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RoomRoute(
    padding: PaddingValues,
    navigateBack: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // 위치 권한 요청 상태
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    
    // 위치 권한이 변경될 때마다 위치 업데이트 시작/중지
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            startLocationUpdates(context) { location ->
                viewModel.onRoomUiAction(RoomUiAction.OnLocationUpdate(location))
            }
        }
    }
    
    // 앱 생명주기 관찰하여 위치 업데이트 관리
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (locationPermissionState.status.isGranted) {
                    startLocationUpdates(context) { location ->
                        viewModel.onRoomUiAction(RoomUiAction.OnLocationUpdate(location))
                    }
                } else {
                    locationPermissionState.launchPermissionRequest()
                }
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                stopLocationUpdates(context)
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

// 위치 콜백 인스턴스를 저장할 전역 변수
private var locationCallback: LocationCallback? = null

// 위치 업데이트 시작
@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    context: Context,
    onLocationUpdate: (LatLng) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    val locationRequest = LocationRequest.Builder(30000) // 30초마다 업데이트
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()
    
    // 이전 콜백이 있다면 제거
    locationCallback?.let {
        fusedLocationClient.removeLocationUpdates(it)
    }
    
    locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                onLocationUpdate(latLng)
            }
        }
    }
    
    // 위치 업데이트 시작
    locationCallback?.let {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            it,
            context.mainLooper
        )
        
        // 즉시 한 번 위치 요청
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(location.latitude, location.longitude)
                onLocationUpdate(latLng)
            }
        }
    }
}

// 위치 업데이트 중지
private fun stopLocationUpdates(context: Context) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    locationCallback?.let {
        fusedLocationClient.removeLocationUpdates(it)
    }
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
    
    // 현재 위치가 업데이트되면 카메라도 업데이트
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.position = com.naver.maps.map.CameraPosition(it, 15.0)
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
                        onMapClick = { /* 맵 클릭 이벤트 처리 */ }
                    )
                }
            }
            
            // 기존 UI 요소들 (높은 z-index)
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