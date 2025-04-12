package com.ikseong.ucompass.main.viewmodel

sealed interface MainUiEvent {
    data object RequestLocationPermissionDialog: MainUiEvent
    data object EditProfileDialog: MainUiEvent
    data object NavigateToRoom : MainUiEvent
    data object NavigateToCreateRoom : MainUiEvent
}