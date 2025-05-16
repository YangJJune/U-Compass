package com.ikseong.ucompass.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.domain.GetRootApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRootApiUseCase: GetRootApiUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            getRootApiUseCase().fold(
                onSuccess = {
                    Log.d("MainViewModel", "onSuccess: $it")
                },
                onFailure = {
                    Log.d("MainViewModel", "fail: $it")
                }
            )
        }
    }

    private val _uiState = MutableStateFlow(MainUiState.dummyDataState)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<MainUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onMainUiAction(action: MainUiAction) {
        when (action) {
            MainUiAction.OnAddressClick -> setLocationPermissionDialogVisible(true)
            MainUiAction.OnAllowLocationClick -> requestLocationPermission()
            MainUiAction.OnDenyLocationClick -> setLocationPermissionDialogVisible(false)

            MainUiAction.OnProfileClick -> setEditProfileDialogVisible(true)
            MainUiAction.OnCloseClick -> setEditProfileDialogVisible(false)
            MainUiAction.OnOpenGalleryClick -> openGallery()
            is MainUiAction.OnEditCompleteClick -> editProfileData(action.name, action.email)

            is MainUiAction.OnRoomClick -> navigateToRoom(action.id)
            is MainUiAction.OnRoomActionClick -> performRoomAction(action.room, action.isHost)
            MainUiAction.OnCreateRoomClick -> navigateToCreateRoom()
        }
    }

    private fun openGallery() {
        viewModelScope.launch {
            _uiEvent.send(MainUiEvent.OpenGallery)
        }
    }

    private fun editProfileData(name: String, email: String) {
        _uiState.update {
            it.copy(
                name = name,
                email = email
            )
        }
        // TODO : 프로필 수정 API
        setEditProfileDialogVisible(false)
    }

    private fun requestLocationPermission() {
        viewModelScope.launch {
            _uiEvent.send(MainUiEvent.RequestLocationPermission)
        }
        setLocationPermissionDialogVisible(false)
    }

    private fun performRoomAction(room: RoomInfo, isHost: Boolean) {
        _uiState.update {
            it.copy(roomList = it.roomList.remove(room))
        }
        if (isHost) {
            // TODO : 방 삭제 API
        } else {
            // TODO : 방 나가기 API
        }

    }

    private fun setLocationPermissionDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isLocationPermissionDialogVisible = flag)
        }
    }

    private fun setEditProfileDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isEditProfileDialogVisible = flag)
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

}