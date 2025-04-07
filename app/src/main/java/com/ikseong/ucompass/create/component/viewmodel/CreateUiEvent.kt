package com.ikseong.ucompass.create.component.viewmodel

sealed interface CreateUiEvent {
    data object NavigateToFinish: CreateUiEvent
    data object NavigateToHome : CreateUiEvent
    data object ShareLink : CreateUiEvent
}