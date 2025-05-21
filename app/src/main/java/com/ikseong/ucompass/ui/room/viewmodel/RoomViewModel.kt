package com.ikseong.ucompass.ui.room.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.domain.DeleteRoomUseCase
import com.ikseong.ucompass.domain.GetRoomItemUseCase
import com.ikseong.ucompass.ui.model.ParticipantInfo
import com.ikseong.ucompass.ui.util.LocationUtil
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val getRoomItemUseCase: GetRoomItemUseCase,
    private val deletionUseCase: DeleteRoomUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<RoomUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // 현재 위치 정보
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    fun onRoomUiAction(action: RoomUiAction) {
        when (action) {
            RoomUiAction.OnBackClick -> navigateBack()
            RoomUiAction.OnDeleteClick -> setRoomDeleteDialogVisible(true)
            RoomUiAction.OnDeleteConfirmClick -> deleteRoom()
            RoomUiAction.OnDeleteCancelClick -> setRoomDeleteDialogVisible(false)
            is RoomUiAction.OnMapToggleClick -> setMapVisible(action.isShown)
            RoomUiAction.OnUserListClick -> showUserListBottomSheet()
            is RoomUiAction.OnUserShownClick -> setUserShown(action.userName)
            is RoomUiAction.OnAllUserShownClick -> setAllUserShown()
            is RoomUiAction.OnLottieClick -> setSearchMode(action.isSearching)
            is RoomUiAction.OnLocationUpdate -> updateCurrentLocation(action.location)
        }
    }

    // 현재 위치 업데이트
    fun updateCurrentLocation(location: LatLng) {
        _currentLocation.value = location
        updateParticipantsDistanceAndDirection(location)
    }

    // 참가자들의 거리와 방향 업데이트
    private fun updateParticipantsDistanceAndDirection(currentLatLng: LatLng) {
        val updatedParticipants = _uiState.value.participantInfo.map { participant ->
            // 참가자의 위치 정보가 있을 경우에만 계산
            if (participant.latitude != 0.0 && participant.longitude != 0.0) {
                val participantLatLng = LatLng(participant.latitude, participant.longitude)
                val distance =
                    LocationUtil.calculateDistanceInMeters(currentLatLng, participantLatLng)
                val direction = LocationUtil.calculateDirection(currentLatLng, participantLatLng)

                participant.copy(
                    direction = direction,
                    distance = distance
                )
            } else {
                participant
            }
        }

        _uiState.update { currentState ->
            currentState.copy(participantInfo = updatedParticipants)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.send(RoomUiEvent.NavigateToBack)
        }
    }

    private fun setAllUserShown() {
        val isAllShown = _uiState.value.participantInfo.all { it.isShown }

        _uiState.update { currentState ->
            currentState.copy(
                participantInfo = currentState.participantInfo.map { info ->
                    info.copy(isShown = !isAllShown)
                })
        }
    }

    private fun setUserShown(userName: String) {
        _uiState.update {
            it.copy(
                participantInfo = it.participantInfo.map { info ->
                    if (info.name == userName) {
                        info.copy(isShown = !info.isShown)
                    } else {
                        info
                    }
                }
            )
        }
    }

    private fun showUserListBottomSheet() {
        viewModelScope.launch {
            _uiEvent.send(RoomUiEvent.ShowBottomSheet)
        }
    }

    private fun setSearchMode(flag: Boolean) {
        _uiState.update {
            it.copy(isSearchMode = flag)
        }
    }

    private fun setMapVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isMapVisible = !flag)
        }
    }

    private fun deleteRoom() {
        viewModelScope.launch {
            val id = _uiState.value.roomId.toInt()
            deletionUseCase(id).fold(
                onSuccess = {
                    Log.d("RoomViewModel", "deleteRoom: $it")
                    setRoomDeleteDialogVisible(false)
                    _uiEvent.send(RoomUiEvent.NavigateToBack)
                },
                onFailure = {
                    Log.e("RoomViewModel", "deleteRoom: $it")
                }
            )
        }

    }

    private fun setRoomDeleteDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isRoomDeleteDialogVisible = flag)
        }
    }

    fun getRoomItem(roomId: Long) {
        viewModelScope.launch {
            getRoomItemUseCase(roomId).fold(
                onSuccess = { data ->
                    _uiState.update {
                        it.copy(
                            roomId = data.id,
                            roomName = data.title,
                            participantInfo = data.participants.map { participantName ->
                                ParticipantInfo(
                                    name = participantName,
                                )
                            }
                        )
                    }
                },
                onFailure = {
                    Log.e("getRoomItem", it.message.toString())
                }
            )
        }
    }
}