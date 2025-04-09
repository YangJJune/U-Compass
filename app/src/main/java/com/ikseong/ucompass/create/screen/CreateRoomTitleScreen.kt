package com.ikseong.ucompass.create.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.ikseong.ucompass.common.component.ObserveAsEvents
import com.ikseong.ucompass.common.component.UCompassButton
import com.ikseong.ucompass.common.component.UCompassLogo
import com.ikseong.ucompass.common.component.UCompassTextField
import com.ikseong.ucompass.create.component.CreateRoomTitleContent
import com.ikseong.ucompass.create.viewmodel.CreateUiAction
import com.ikseong.ucompass.create.viewmodel.CreateUiEvent
import com.ikseong.ucompass.create.viewmodel.CreateUiState
import com.ikseong.ucompass.create.viewmodel.CreateViewModel
import com.ikseong.ucompass.ui.theme.UCompassTheme

@Composable
fun CreateRoomTitleRoute(
    padding: PaddingValues,
    navigateToFinish: () -> Unit,
    viewModel: CreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.uiEvent) { event ->
        when (event) {
            CreateUiEvent.NavigateToFinish -> navigateToFinish()
            else -> {}
        }
    }

    CreateRoomTitleScreen(
        padding = padding,
        uiState = uiState,
        onAction = viewModel::onCreateUiAction
    )

}

@Composable
fun CreateRoomTitleScreen(
    padding: PaddingValues,
    uiState: CreateUiState,
    onAction: (CreateUiAction) -> Unit
) {
    val title = uiState.title

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
            Spacer(modifier = Modifier.height(120.dp))
            CreateRoomTitleContent()
            UCompassTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .padding(horizontal = 25.dp),
                text = title,
                placeholder = "방 제목을 입력하세요.",
                verticalPadding = 15.dp,
                onValueChange = { onAction(CreateUiAction.UpdateTitleField(it)) }
            )
        }

        UCompassButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 21.dp)
                .height(62.dp)
                .align(Alignment.BottomCenter),
            text = "생성하기",
            fontSize = 20.sp,
            color = Color(0xFF00E397)
        ) { onAction(CreateUiAction.OnCreateClick) }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateRoomTitleScreenPreview() {
    UCompassTheme {
        CreateRoomTitleScreen(
            padding = PaddingValues(0.dp),
            uiState = CreateUiState(),
            onAction = {},
        )
    }
}