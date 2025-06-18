package com.ikseong.ucompass.ui.main.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.data.repository.AddressRepository
import com.ikseong.ucompass.domain.DeleteRoomUseCase
import com.ikseong.ucompass.domain.EditUserUseCase
import com.ikseong.ucompass.domain.GetDeviceIdUseCase
import com.ikseong.ucompass.domain.GetRoomListUseCase
import com.ikseong.ucompass.domain.GetUserNameUseCase
import com.ikseong.ucompass.domain.LeaveRoomUseCase
import com.ikseong.ucompass.domain.SaveDeviceIdUseCase
import com.ikseong.ucompass.domain.SaveUserNameUseCase
import com.ikseong.ucompass.mapper.toRoomInfo
import com.ikseong.ucompass.ui.util.LocationUtil.getCurrentLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRoomListUseCase: GetRoomListUseCase,
    private val deleteRoomUseCase: DeleteRoomUseCase,
    private val leaveRoomUseCase: LeaveRoomUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val saveDeviceIdUseCase: SaveDeviceIdUseCase,
    private val saveUserNameUseCase: SaveUserNameUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val editUserUseCase: EditUserUseCase,
    private val addressRepository: AddressRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<MainUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _deviceId = MutableStateFlow("")

    init {
        saveDeviceId()
        getUserName()
        getLocationAndAddress()
    }

    private fun getLocationAndAddress() {
        viewModelScope.launch {
            try {
                val location = getCurrentLocation(context = context)
                // 위치 정보를 가져온 후 주소 변환
                addressRepository.getAddressFromCoordinates(
                    latitude = location.latitude,
                    longitude = location.longitude
                ).fold(
                    onSuccess = { address ->
                        _uiState.update {
                            it.copy(address = address)
                        }
                        Log.d("MainViewModel", "현재 주소: $address")
                    },
                    onFailure = { exception ->
                        Log.e("MainViewModel", "주소 변환 실패: ${exception.message}")
                        _uiState.update {
                            it.copy(address = "주소를 불러올 수 없습니다")
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "위치 정보 가져오기 실패: ${e.message}")
                _uiState.update {
                    it.copy(address = "위치를 불러올 수 없습니다")
                }
            }
        }
    }

    private fun getUserName() {
        viewModelScope.launch {
            getUserNameUseCase().collect { name ->
                _uiState.update {
                    it.copy(name = name ?: "Guest")
                }
            }
        }
    }

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

    @OptIn(ExperimentalUuidApi::class)
    private fun saveDeviceId() {
        val uuid = Uuid.random().toString()

        viewModelScope.launch {
            saveDeviceIdUseCase(uuid)
            getDeviceIdUseCase().collect { deviceId ->
                if (deviceId != null) {
                    _deviceId.value = deviceId
                } else {
                    _deviceId.value = uuid
                }
            }
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
        viewModelScope.launch {
            val deviceId = getDeviceIdUseCase().firstOrNull()
            if (deviceId != null) {
                editUserUseCase(
                    deviceId = deviceId,
                    name = name
                ).fold(
                    onSuccess = {
                        saveUserNameUseCase(name)
                        Log.e("EditUser", "Edit User Success")
                    },
                    onFailure = {
                        Log.e("EditUser", it.message.toString())
                    }
                )
            } else {
                Log.e("EditUser", "deviceId is null")
            }
        }
        setEditProfileDialogVisible(false)
    }

    private fun requestLocationPermission() {
        viewModelScope.launch {
            _uiEvent.send(MainUiEvent.RequestLocationPermission)
        }
        setLocationPermissionDialogVisible(false)
    }

    private fun performRoomAction(room: RoomInfo, isHost: Boolean) {
        viewModelScope.launch {
            if (isHost) {
                deleteRoomUseCase(room.roomId.toInt()).fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(roomList = it.roomList.remove(room))
                        }
                    },
                    onFailure = {
                        Log.e("performRoomAction", it.message.toString())
                    }
                )
            } else {
                getDeviceIdUseCase().collect {
                    _deviceId.value = it!!
                    leaveRoomUseCase(
                        deviceId = _deviceId.value,
                        roomId = room.roomId
                    ).fold(
                        onSuccess = {
                            _uiState.update {
                                it.copy(roomList = it.roomList.remove(room))
                            }
                        },
                        onFailure = {
                            Log.e("performRoomAction", it.message.toString())
                        }
                    )
                }
            }
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

    fun fetchRoomList() {
        viewModelScope.launch {
            getRoomListUseCase().fold(
                onSuccess = { data ->
                    _uiState.update {
                        it.copy(
                            roomList = data.map { it.toRoomInfo() }.toPersistentList()
                        )
                    }
                },
                onFailure = {
                    Log.e("fetchRoomList", it.message.toString())
                }
            )
        }
    }

    /**
     * 현재 위치 기반으로 주소를 새로고침하는 함수
     */
    fun refreshCurrentAddress() {
        getLocationAndAddress()
    }

}