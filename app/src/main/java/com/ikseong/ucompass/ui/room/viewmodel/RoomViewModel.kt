package com.ikseong.ucompass.ui.room.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<RoomUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onRoomUiAction(action: RoomUiAction) {
        when (action) {
            RoomUiAction.OnBackClick -> {}
            RoomUiAction.OnDeleteClick -> {}
            RoomUiAction.OnDeleteConfirmClick -> {}
            RoomUiAction.OnDeleteCancelClick -> {}
            RoomUiAction.OnMapToggleClick -> {}
            RoomUiAction.OnUserListClick -> {}
            is RoomUiAction.OnUserShownClick -> {}
            is RoomUiAction.OnAllUserShownClick -> {}
            RoomUiAction.OnLottieClick -> {}
            
        }
    }
}