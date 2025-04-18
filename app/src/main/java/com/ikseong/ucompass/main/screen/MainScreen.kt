package com.ikseong.ucompass.main.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ikseong.ucompass.common.component.ObserveAsEvents
import com.ikseong.ucompass.common.component.UCompassButton
import com.ikseong.ucompass.common.component.UCompassLogo
import com.ikseong.ucompass.main.component.MainRoomList
import com.ikseong.ucompass.main.component.MainUserContent
import com.ikseong.ucompass.main.viewmodel.MainUiAction
import com.ikseong.ucompass.main.viewmodel.MainUiEvent
import com.ikseong.ucompass.main.viewmodel.MainUiState
import com.ikseong.ucompass.main.viewmodel.MainViewModel
import com.ikseong.ucompass.main.viewmodel.RoomInfo
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import kotlinx.collections.immutable.persistentListOf


@Composable
fun MainRoute(
    padding: PaddingValues,
    navigateToRoom: (Long) -> Unit = {},
    navigateToCreateRoom: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            MainUiEvent.EditProfileDialog -> { /*show Dialog*/
            }

            MainUiEvent.NavigateToCreateRoom -> navigateToCreateRoom()
            is MainUiEvent.NavigateToRoom -> navigateToRoom(event.id)
            MainUiEvent.RequestLocationPermissionDialog -> { /*show Dialog*/
            }
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
