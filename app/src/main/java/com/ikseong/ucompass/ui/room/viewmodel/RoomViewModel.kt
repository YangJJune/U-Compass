package com.ikseong.ucompass.ui.room.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
            RoomUiAction.OnDeleteClick -> setRoomDeleteDialogVisible(true)
            RoomUiAction.OnDeleteConfirmClick -> deleteRoom()
            RoomUiAction.OnDeleteCancelClick -> setRoomDeleteDialogVisible(false)
            RoomUiAction.OnMapToggleClick -> {}
            RoomUiAction.OnUserListClick -> {}
            is RoomUiAction.OnUserShownClick -> {}
            is RoomUiAction.OnAllUserShownClick -> {}
            RoomUiAction.OnLottieClick -> {}
            
        }
    }

    private fun deleteRoom() {
        // TODO : isHost 에 따라 방 나가기/삭제하기 API
    }

    private fun setRoomDeleteDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isRoomDeleteDialogVisible = flag)
        }
    }
}