package com.ikseong.ucompass.ui.room.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.ObserveAsEvents
import com.ikseong.ucompass.ui.room.component.RoomGuideComponent
import com.ikseong.ucompass.ui.room.component.RoomTopComponent
import com.ikseong.ucompass.ui.room.viewmodel.ParticipantInfo
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiAction
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiEvent
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiState
import com.ikseong.ucompass.ui.room.viewmodel.RoomViewModel
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun RoomRoute(
    padding: PaddingValues,
    navigateBack: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            RoomUiEvent.NavigateToBack -> navigateBack()
        }
    }

    RoomScreen(
        padding = padding,
        uiState = uiState,
        onAction = viewModel::onRoomUiAction
    )
}


@Composable
fun RoomScreen(
    padding: PaddingValues,
    uiState: RoomUiState,
    onAction: (RoomUiAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoomTopComponent(
                onBackClick = { RoomUiAction.OnBackClick },
                onDeleteClick = { RoomUiAction.OnDeleteClick },
                roomName = uiState.roomName
            )
            RoomGuideComponent(
                modifier = Modifier.padding(top = 64.dp),
                address = uiState.address,
            )

            Box(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .size(323.dp)
                    .background(Color(0xFF00E397))
            )
        }
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
                .clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_room_participant_40),
                contentDescription = null,
                tint = Color(0x8000E397)
            )
            Text(
                text = "참가 인원 ${uiState.participantInfo.size}명",
                style = typography.medium.copy(
                    fontSize = 18.sp,
                    color = Color(0xFF606060)
                )
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
private fun RoomScreenPreview() {
    RoomScreen(
        padding = PaddingValues(0.dp),
        uiState = RoomUiState(
            roomName = "Room Name",
            address = "123 Main St, City, Country",
            participantInfo = listOf(
                ParticipantInfo(
                    name = "John Doe",
                    profileUrl = "https://example.com/profile.jpg",
                    direction = "NE",
                    distance = 1000,
                    isShown = true,
                ),
                ParticipantInfo(
                    name = "Jane Smith",
                    profileUrl = "https://example.com/profile2.jpg",
                    direction = "SW",
                    distance = 1500,
                    isShown = false,
                ),
                ParticipantInfo(
                    name = "Alice Johnson",
                    profileUrl = "https://example.com/profile3.jpg",
                    direction = "NW",
                    distance = 2000,
                    isShown = true,
                )
            ),
        ),
        onAction = {}
    )
}