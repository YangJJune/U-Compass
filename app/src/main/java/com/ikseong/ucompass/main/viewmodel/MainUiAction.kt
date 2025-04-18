package com.ikseong.ucompass.main.viewmodel

sealed interface MainUiAction {
    data object OnAddressClick : MainUiAction
    data object OnAllowLocationClick : MainUiAction
    data object OnDenyLocationClick : MainUiAction

    data object OnProfileClick : MainUiAction
    data object OnCloseClick : MainUiAction
    data object OnOpenGalleryClick : MainUiAction
    data class OnEditCompleteClick(val name: String, val email: String) : MainUiAction

    data object OnCreateRoomClick : MainUiAction
    data class OnRoomClick(val id: Long) : MainUiAction
    data class OnRoomActionClick(val room: RoomInfo, val isHost: Boolean) : MainUiAction
}