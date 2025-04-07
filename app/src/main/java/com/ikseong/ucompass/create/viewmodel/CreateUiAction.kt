package com.ikseong.ucompass.create.viewmodel

sealed interface CreateUiAction {
    data object OnCreateClick: CreateUiAction
    data object OnConfirmClick : CreateUiAction
    data object OnShareClick : CreateUiAction
}