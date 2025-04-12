package com.ikseong.ucompass.main.viewmodel

sealed interface MainUiAction {
    data object OnProfileClick : MainUiAction
    data object OnAddressClick : MainUiAction
    data object OnCreateRoomClick : MainUiAction
    data class OnRoomClick(val id: Long) : MainUiAction
}