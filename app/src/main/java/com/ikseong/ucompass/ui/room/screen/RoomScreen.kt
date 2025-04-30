package com.ikseong.ucompass.ui.room.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikseong.ucompass.ui.common.component.ObserveAsEvents
import com.ikseong.ucompass.ui.room.component.RoomDefaultContent
import com.ikseong.ucompass.ui.room.component.RoomDeleteDialog
import com.ikseong.ucompass.ui.room.component.RoomTopComponent
import com.ikseong.ucompass.ui.room.viewmodel.ParticipantInfo
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiAction
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiEvent
import com.ikseong.ucompass.ui.room.viewmodel.RoomUiState
import com.ikseong.ucompass.ui.room.viewmodel.RoomViewModel

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
        if (uiState.isSearchMode && uiState.isMapVisible) {
            // TODO: NaverMap 화면에 띄우기
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoomTopComponent(
                onBackClick = { onAction(RoomUiAction.OnBackClick) },
                onDeleteClick = { onAction(RoomUiAction.OnDeleteClick) },
                isSearchMode = uiState.isSearchMode,
                roomName = uiState.roomName
            )
            if (!uiState.isSearchMode) {
                RoomDefaultContent(
                    address = uiState.address,
                    participantCount = uiState.participantInfo.size
                )
            } else {
//                RoomSearchContent()
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