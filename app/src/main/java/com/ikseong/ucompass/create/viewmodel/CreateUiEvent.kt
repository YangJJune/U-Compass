package com.ikseong.ucompass.create.viewmodel

sealed interface CreateUiEvent {
    data object NavigateToFinish: CreateUiEvent
    data object NavigateToHome : CreateUiEvent
    data object ShareLink : CreateUiEvent
}