package com.ikseong.ucompass.main.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ikseong.ucompass.common.component.ObserveAsEvents
import com.ikseong.ucompass.main.viewmodel.MainUiAction
import com.ikseong.ucompass.main.viewmodel.MainUiEvent
import com.ikseong.ucompass.main.viewmodel.MainUiState
import com.ikseong.ucompass.main.viewmodel.MainViewModel


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

}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen(
        padding = PaddingValues(0.dp),
        uiState = MainUiState(),
        onAction = {}
    )
}
