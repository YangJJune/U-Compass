package com.ikseong.ucompass.main.viewmodel

sealed interface MainUiEvent {
    data object RequestLocationPermission : MainUiEvent
    data object EditProfileDialog : MainUiEvent
    data class NavigateToRoom(val id: Long) : MainUiEvent
    data object NavigateToCreateRoom : MainUiEvent
    data object OpenGallery : MainUiEvent
}