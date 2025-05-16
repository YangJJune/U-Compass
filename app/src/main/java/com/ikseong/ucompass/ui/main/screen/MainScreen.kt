package com.ikseong.ucompass.ui.main.screen

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ikseong.ucompass.ui.common.component.ObserveAsEvents
import com.ikseong.ucompass.ui.common.component.UCompassButton
import com.ikseong.ucompass.ui.common.component.UCompassLogo
import com.ikseong.ucompass.ui.main.component.EditProfileDialog
import com.ikseong.ucompass.ui.main.component.MainRoomList
import com.ikseong.ucompass.ui.main.component.MainUserContent
import com.ikseong.ucompass.ui.main.viewmodel.MainUiAction
import com.ikseong.ucompass.ui.main.viewmodel.MainUiEvent
import com.ikseong.ucompass.ui.main.viewmodel.MainUiState
import com.ikseong.ucompass.ui.main.viewmodel.MainViewModel
import com.ikseong.ucompass.ui.main.viewmodel.RoomInfo
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import kotlinx.collections.immutable.persistentListOf


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainRoute(
    padding: PaddingValues,
    navigateToRoom: (Long) -> Unit = {},
    navigateToCreateRoom: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 위치 권한 요청 상태
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    
    // 앱 생명주기 관찰하여 위치 권한 요청 관리
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (!locationPermissionState.status.isGranted && 
                    !locationPermissionState.status.shouldShowRationale) {
                    // 권한이 없고, 이전에 거부된 적이 없으면 요청
                    locationPermissionState.launchPermissionRequest()
                }
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // 사용자가 주소를 클릭했을 때 권한 요청
    LaunchedEffect(uiState.isLocationPermissionDialogVisible) {
        if (uiState.isLocationPermissionDialogVisible) {
            if (!locationPermissionState.status.isGranted) {
                locationPermissionState.launchPermissionRequest()
            }
            // 다이얼로그 상태 초기화
            viewModel.onMainUiAction(MainUiAction.OnDenyLocationClick)
        }
    }

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            MainUiEvent.EditProfileDialog -> { /*show Dialog*/
            }

            MainUiEvent.NavigateToCreateRoom -> navigateToCreateRoom()
            is MainUiEvent.NavigateToRoom -> navigateToRoom(event.id)
            MainUiEvent.RequestLocationPermission -> {
                if (!locationPermissionState.status.isGranted) {
                    locationPermissionState.launchPermissionRequest()
                }
            }

            MainUiEvent.OpenGallery -> {}// TODO: 갤러리 열기
        }
    }
    MainScreen(
        padding = padding,
        uiState = uiState,
        onAction = viewModel::onMainUiAction
    )
}

@Composable
fun MainScreen(
    padding: PaddingValues,
    uiState: MainUiState,
    onAction: (MainUiAction) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
        ) {
            UCompassLogo()
            Spacer(modifier = Modifier.height(12.dp))

            MainUserContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                userName = uiState.name,
                address = uiState.address,
                imageUrl = uiState.profileUrl,
                onAddressClick = { onAction(MainUiAction.OnAddressClick) },
                onProfileClick = { onAction(MainUiAction.OnProfileClick) }
            )

            Text(
                modifier = Modifier.padding(start = 21.dp, top = 48.dp),
                text = "참여한 컴패스 리스트",
                style = typography.semiBold.copy(
                    fontSize = 24.sp,
                )
            )

            MainRoomList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 21.dp, vertical = 32.dp),
                roomList = uiState.roomList,
                onRoomClick = { onAction(MainUiAction.OnRoomClick(it)) },
                onActionClick = { roomInfo, isHost ->
                    onAction(MainUiAction.OnRoomActionClick(roomInfo, isHost))
                }
            )

        }

        UCompassButton(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FFFD))
                .padding(horizontal = 22.dp, vertical = 15.dp)
                .height(62.dp)
                .align(Alignment.BottomCenter),
            text = "생성하기",
            fontSize = 20.sp,
            color = Color(0xFF00E397)
        ) { onAction(MainUiAction.OnCreateRoomClick) }

        if (uiState.isEditProfileDialogVisible) {
            EditProfileDialog(
                profileImgUrl = "",
                onComplete = { name, email ->
                    onAction(MainUiAction.OnEditCompleteClick(name, email))
                },
                onEditProfileImgClick = {
                    // TODO: Open Gallery
                },
                onDismissRequest = { onAction(MainUiAction.OnCloseClick) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        padding = PaddingValues(0.dp),
        uiState = MainUiState(
            name = "김익성",
            address = "서울특별시 강남구 역삼동",
            profileUrl = "",
            roomList = persistentListOf(
                RoomInfo(
                    hostName = "HostName1",
                    roomLink = "RoomLink1",
                    roomName = "RoomName1"
                ),
                RoomInfo(
                    hostName = "HostName2",
                    roomLink = "RoomLink2",
                    roomName = "RoomName2"
                ),
                RoomInfo(
                    hostName = "HostName3",
                    roomLink = "RoomLink3",
                    roomName = "RoomName3"
                ),
                RoomInfo(
                    hostName = "HostName4",
                    roomLink = "RoomLink4",
                    roomName = "RoomName4"
                )
            )
        ),
        onAction = {}
    )
}
