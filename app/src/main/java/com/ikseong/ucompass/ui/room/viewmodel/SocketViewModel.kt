package com.ikseong.ucompass.ui.room.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.data.socket.repository.SocketRepository
import com.ikseong.ucompass.data.socket.repository.UserLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(
    private val socketRepository: SocketRepository
) : ViewModel() {
    private val _locationData = MutableStateFlow<Map<String, UserLocation>>(emptyMap())
    val locationData: StateFlow<Map<String, UserLocation>> = _locationData

    fun getLocationData() {
        viewModelScope.launch {
            socketRepository.flowConnect()
            while (isActive) {
                socketRepository.startReceiving().collect { dto ->
                    _locationData.update { current ->
                        //위치 갱신 및 유저 위치 추가
                        current + (dto.deviceId to UserLocation(dto.lat, dto.lng))
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketRepository.disconnect()
    }
}