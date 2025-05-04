package com.ikseong.ucompass.ui.room.viewmodel


interface RoomUiAction {
    data object OnBackClick : RoomUiAction
    data object OnDeleteClick : RoomUiAction
    data object OnDeleteConfirmClick : RoomUiAction
    data object OnDeleteCancelClick : RoomUiAction
    data class OnMapToggleClick(val isShown: Boolean) : RoomUiAction
    data object OnUserListClick : RoomUiAction
    data class OnUserShownClick(val userName: String) : RoomUiAction
    data class OnAllUserShownClick(val isShown: Boolean) : RoomUiAction
    data class OnLottieClick(val isSearching: Boolean) : RoomUiAction
}