package com.ikseong.ucompass.create.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CreateViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<CreateUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onCreateUiAction(action: CreateUiAction) {
        when(action) {
            CreateUiAction.OnConfirmClick -> TODO()
            CreateUiAction.OnCreateClick -> TODO()
            CreateUiAction.OnShareClick -> TODO()
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

}

