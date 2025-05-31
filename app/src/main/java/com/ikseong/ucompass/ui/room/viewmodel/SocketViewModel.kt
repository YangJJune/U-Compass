package com.ikseong.ucompass.ui.room.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.data.network.socket.UserLocation
import com.ikseong.ucompass.data.socket.repository.SocketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(
    private val socketRepository: SocketRepository
) : ViewModel() {
    val userLocations: StateFlow<Map<String, UserLocation>> = socketRepository.locationDataFlow

    fun connectSocket() {
        viewModelScope.launch {
            socketRepository.connect()
        }
    }

    fun login(userId: String, roomId: Int) {
        viewModelScope.launch {
            socketRepository.login(userId, roomId)
        }
    }

    fun sendLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            socketRepository.sendLocation(lat, lng)
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketRepository.disconnect()
    }
}