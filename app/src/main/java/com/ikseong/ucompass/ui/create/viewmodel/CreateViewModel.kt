package com.ikseong.ucompass.ui.create.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CreateViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<CreateUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onCreateUiAction(action: com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction) {
        when (action) {
            com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction.OnConfirmClick -> navigateToFinishScreen()
            com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction.OnCreateClick -> navigateToHomeScreen()
            com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction.OnShareClick -> shareRoomInfo()
            is com.ikseong.ucompass.ui.create.viewmodel.CreateUiAction.UpdateTitleField -> updateTitle(action.text)
        }
    }

    private fun updateTitle(text: String) {
        _uiState.value = _uiState.value.copy(title = text)
    }

    private fun navigateToFinishScreen() {
        viewModelScope.launch {
            _uiEvent.send(CreateUiEvent.NavigateToFinish)
        }
    }

    private fun navigateToHomeScreen() {
        viewModelScope.launch {
            _uiEvent.send(CreateUiEvent.NavigateToHome)
        }
    }

    private fun shareRoomInfo() {
        viewModelScope.launch {
            _uiEvent.send(CreateUiEvent.ShareLink)
        }
    }


}

