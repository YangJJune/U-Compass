package com.ikseong.ucompass.create.viewmodel

sealed interface CreateUiAction {
    data object OnCreateClick: CreateUiAction
    data object OnConfirmClick : CreateUiAction
    data object OnShareClick : CreateUiAction
    data class UpdateTitleField(val text: String) : CreateUiAction
}