package com.ikseong.ucompass.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<MainUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onCreateUiAction(action: MainUiAction) {
        when (action) {
            MainUiAction.OnAddressClick -> TODO()
            MainUiAction.OnCreateRoomClick -> TODO()
            MainUiAction.OnProfileClick -> TODO()
            is MainUiAction.OnRoomClick -> navigateToRoom(action.id)
        }
    }

    private fun showLocationPermissionDialog() {
        viewModelScope.launch {
            _uiEvent.send(MainUiEvent.RequestLocationPermissionDialog)
        }
    }

    private fun navigateToCreateRoom() {
        viewModelScope.launch {
            _uiEvent.send(MainUiEvent.NavigateToCreateRoom)
        }
    }

    private fun navigateToRoom(id: Long) {
        viewModelScope.launch {
            _uiEvent.send(MainUiEvent.NavigateToRoom(id))
        }
    }

    private fun showEditProfileDialog() {
        viewModelScope.launch {
            _uiEvent.send(MainUiEvent.EditProfileDialog)
        }
    }
}