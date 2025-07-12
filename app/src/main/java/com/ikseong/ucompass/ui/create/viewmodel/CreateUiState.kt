package com.ikseong.ucompass.ui.create.viewmodel

data class CreateUiState(
    val title: String = "",
    val link: String = "",
    val roomNumber: Int = 0,
    val isLoading: Boolean = false,
)
