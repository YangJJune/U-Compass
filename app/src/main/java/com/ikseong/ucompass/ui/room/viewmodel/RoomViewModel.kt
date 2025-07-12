package com.ikseong.ucompass.ui.room.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.data.repository.AddressRepository
import com.ikseong.ucompass.data.socket.repository.SocketRepository
import com.ikseong.ucompass.domain.DeleteRoomUseCase
import com.ikseong.ucompass.domain.GetDeviceIdUseCase
import com.ikseong.ucompass.domain.GetRoomItemUseCase
import com.ikseong.ucompass.domain.LeaveRoomUseCase
import com.ikseong.ucompass.ui.common.component.MapMarker
import com.ikseong.ucompass.ui.util.LocationUtil
import com.ikseong.ucompass.ui.util.LocationUtil.calculateDistanceInMeters
import com.ikseong.ucompass.ui.util.LocationUtil.getCurrentLocation
import com.ikseong.ucompass.ui.util.MapParticipantUtil.getRelativeBearing
import com.ikseong.ucompass.ui.util.MapParticipantUtil.rectangleSideAndDistance
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val getRoomItemUseCase: GetRoomItemUseCase,
    private val deleteRoomUseCase: DeleteRoomUseCase,
    private val leaveRoomUseCase: LeaveRoomUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val socketRepository: SocketRepository,
    private val addressRepository: AddressRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<RoomUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _deviceId = MutableStateFlow("")

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
            is RoomUiAction.OnLottieClick -> startSearch(action.isSearching)
            is RoomUiAction.OnLocationUpdate -> updateCurrentLocation(
                action.location,
                action.orientation
            )

            is RoomUiAction.OnUpdateWidthHeight -> {
                _uiState.update {
                    it.copy(
                        contentWidthPx = action.widthPx,
                        contentHeightPx = action.heightPx
                    )
                }
            }
        }
    }

    init {
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

    // 현재 위치 업데이트
    fun updateCurrentLocation(location: LatLng, orientation: Double) {
        _currentLocation.value = location
        // getLocationAndAddress()
        updateParticipantsDistanceAndDirection(location, orientation)
    }

    // 참가자들의 거리와 방향 업데이트
    private fun updateParticipantsDistanceAndDirection(currentLatLng: LatLng, orientation: Double) {
        val updatedParticipants = _uiState.value.participantState.map { participant ->
            // 참가자의 위치 정보가 있을 경우에만 계산
            val participantLatLng = LatLng(participant.latitude, participant.longitude)
            val distance =
                LocationUtil.calculateDistanceInMeters(currentLatLng, participantLatLng)
            val direction = LocationUtil.calculateDirection(currentLatLng, participantLatLng)

            participant.copy(
                distance = distance,
                direction = direction
            )

        }

        val nMapMarkers = _uiState.value.participantState
            .filter { it.isShown }
            .map { participant ->
                val distance = calculateDistanceInMeters(
                    currentLatLng,
                    LatLng(participant.latitude, participant.longitude)
                )
                MapMarker(
                    name = participant.name,
                    latitude = participant.latitude,
                    longitude = participant.longitude,
                    isVisible = participant.isShown,
                    distance = distance,
                ).apply {
                    val angle = getRelativeBearing(
                        currentLatLng.latitude,
                        currentLatLng.longitude,
                        orientation,
                        this.latitude,
                        this.longitude
                    ).toFloat()
                    val (pinDirection, alignDirection, padding) = rectangleSideAndDistance(
                        uiState.value.contentWidthPx.toDouble(),
                        uiState.value.contentHeightPx.toDouble(),
                        angle.toDouble()
                    )
                    this.angle = angle
                    this.pinDirection = pinDirection
                    this.alignDirection = alignDirection
                    this.padding = padding
                }
            }

        _uiState.update { currentState ->
            currentState.copy(
                participantState = updatedParticipants,
                mapMarkers = nMapMarkers
            )
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.send(RoomUiEvent.NavigateToBack)
        }
    }

    private fun setAllUserShown() {
        val isAllShown = _uiState.value.participantState.all { it.isShown }

        _uiState.update { currentState ->
            currentState.copy(
                participantState = currentState.participantState.map { info ->
                    info.copy(isShown = !isAllShown)
                })
        }
    }

    private fun setUserShown(userName: String) {
        _uiState.update {
            it.copy(
                participantState = it.participantState.map { info ->
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

    private fun startSearch(flag: Boolean) {
        _uiState.update {
            it.copy(isSearchMode = flag)
        }
        connectSocket()
    }

    private fun setMapVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isMapVisible = !flag)
        }
    }

    private fun deleteRoom() {
        viewModelScope.launch {
            if (_uiState.value.isHost) {
                deleteRoomUseCase(_uiState.value.roomId.toInt()).fold(
                    onSuccess = {
                        Log.d("RoomViewModel", "deleteRoom: $it")
                        setRoomDeleteDialogVisible(false)
                        _uiEvent.send(RoomUiEvent.NavigateToBack)
                    },
                    onFailure = {
                        Log.e("RoomViewModel", "deleteRoom: $it")
                    }
                )
            } else {
                getDeviceIdUseCase().collect {
                    Log.d("RoomViewModel", "deleteRoom: $it")
                    _deviceId.value = it!!
                    leaveRoomUseCase(
                        deviceId = _deviceId.value,
                        roomId = _uiState.value.roomId
                    ).fold(
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
        }
    }

    private fun setRoomDeleteDialogVisible(flag: Boolean) {
        _uiState.update {
            it.copy(isRoomDeleteDialogVisible = flag)
        }
    }

    fun getRoomItem(roomId: Long) {
        viewModelScope.launch {
            // 방 정보 로딩 시작
            _uiState.update { it.copy(isLoadingRoomInfo = true) }
            
            getRoomItemUseCase(roomId).fold(
                onSuccess = { data ->
                    _uiState.update {
                        it.copy(
                            roomId = data.id.toLong(),
                            roomName = data.title,
                            participantCount = data.participants.size,
                            isLoadingRoomInfo = false
                        )
                    }
                },
                onFailure = {
                    Log.e("getRoomItem", it.message.toString())
                    _uiState.update { it.copy(isLoadingRoomInfo = false) }
                }
            )
        }
    }

    private fun connectSocket() {
        viewModelScope.launch() {
            // 소켓 연결 시작
            _uiState.update { it.copy(isConnectingSocket = true) }
            
            socketRepository.connect()
            socketRepository.login(
                userId = getDeviceIdUseCase().firstOrNull().toString(),
                roomId = /*_uiState.value.roomId.toInt()*/2
            )
            
            // 소켓 연결 완료
            _uiState.update { it.copy(isConnectingSocket = false) }
            
            var previousLocation: LatLng? = null
            var stationaryCount = 0
            
            while (isActive) {
                val currentLocation = _currentLocation.value
                
                socketRepository.sendLocation(
                    currentLocation?.latitude ?: 0.0,
                    currentLocation?.longitude ?: 0.0
                )
                
                // 적응형 업데이트 주기 계산
                val updateInterval = if (currentLocation != null && previousLocation != null) {
                    val distance = calculateDistanceInMeters(previousLocation, currentLocation)
                    if (distance < 5) { // 5m 이하 이동 시 정지 상태로 간주
                        stationaryCount++
                        // 정지 상태가 지속될수록 업데이트 주기를 늘림 (최대 30초)
                        minOf(10000L + (stationaryCount * 5000L), 30000L)
                    } else {
                        stationaryCount = 0
                        10000L // 기본 10초
                    }
                } else {
                    10000L // 기본 10초
                }
                
                previousLocation = currentLocation
                delay(updateInterval)
            }
        }
        viewModelScope.launch {
            socketRepository.locationDataFlow.collect { locations ->
                val participants = locations.map { location ->
                    ParticipantState(
                        name = location.value.name,
                        latitude = location.value.lat,
                        longitude = location.value.lng,
                        distance = _currentLocation.value?.let {
                            LocationUtil.calculateDistanceInMeters(
                                it,
                                LatLng(location.value.lat, location.value.lng)
                            )
                        },
                        direction = _currentLocation.value?.let {
                            LocationUtil.calculateDirection(
                                it,
                                LatLng(location.value.lat, location.value.lng)
                            )
                        }
                    )
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        participantState = participants,
                        participantCount = participants.size,
                    )
                }
            }
        }
    }

    override fun onCleared() {
        socketRepository.disconnect()
        super.onCleared()
    }
}