package com.ikseong.ucompass.ui.create.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikseong.ucompass.ui.common.component.ObserveAsEvents
import com.ikseong.ucompass.ui.common.component.UCompassButton
import com.ikseong.ucompass.ui.common.component.UCompassLogo
import com.ikseong.ucompass.ui.create.component.CreateRoomFinishContent
import com.ikseong.ucompass.ui.create.component.CreateRoomStatus
import com.ikseong.ucompass.ui.create.viewmodel.CreateUiEvent
import com.ikseong.ucompass.ui.create.viewmodel.CreateUiState
import com.ikseong.ucompass.ui.create.viewmodel.CreateViewModel
import com.ikseong.ucompass.ui.theme.UCompassTheme

@Composable
fun CreateRoomFinishRoute(
    padding: PaddingValues,
    navigateToHome: () -> Unit = {},
    shareRoomLink: () -> Unit = {},
    viewModel: CreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            CreateUiEvent.NavigateToHome -> navigateToHome()
            CreateUiEvent.ShareLink -> shareRoomLink() // TODO
            else -> {}
        }
    }

    CreateRoomFinishScreen(
        padding = padding,
        uiState = uiState,
        onAction = viewModel::onCreateUiAction
    )
}

@Composable
fun CreateRoomFinishScreen(
    padding: PaddingValues,
    uiState: CreateUiState,
    onAction: (com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction) -> Unit
) {
    val roomTitle = uiState.title
    val copyLink = uiState.link

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
        ) {
            UCompassLogo()
            Spacer(modifier = Modifier.height(101.dp))
            CreateRoomFinishContent()
            CreateRoomStatus(
                roomTitle = roomTitle,
                copyLink = uiState.roomNumber.toString()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 21.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UCompassButton(
                modifier = Modifier
                    .weight(114f)
                    .height(62.dp),
                text = "공유",
                color = Color(0xFFD9D9D9),
                fontSize = 20.sp
            ) { onAction(com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction.OnShareClick) }
            Spacer(modifier = Modifier.weight(12f))
            UCompassButton(
                modifier = Modifier
                    .weight(242f)
                    .height(62.dp),
                text = "확인",
                color = Color(0xFF00E397),
                fontSize = 20.sp
            ) { onAction(com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction.OnConfirmClick) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateRoomFinishScreenPreview() {
    UCompassTheme {
        CreateRoomFinishScreen(
            padding = PaddingValues(),
            uiState = CreateUiState(),
            onAction = {}
        )
    }
}