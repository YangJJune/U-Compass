package com.ikseong.ucompass.create.component.viewmodel

sealed interface CreateUiAction {
    data object OnCreateClick: CreateUiAction
    data object OnConfirmClick : CreateUiAction
    data object OnShareClick : CreateUiAction
}