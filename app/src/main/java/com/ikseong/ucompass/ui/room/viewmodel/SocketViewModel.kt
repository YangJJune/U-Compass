package com.ikseong.ucompass.ui.room.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.data.socket.repository.SocketRepository
import com.ikseong.ucompass.data.socket.repository.UserLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(
    private val socketRepository: SocketRepository
) : ViewModel() {

    val data: MutableMap<String, UserLocation> = mutableMapOf()
    fun getLocationData() {
        viewModelScope.launch {
            //구독
            socketRepository.locationDto.collect { dto ->
                if(!data.containsKey(dto.deviceId)){
                    data.set(dto.deviceId, UserLocation(dto.lat,dto.lng))
                }
            }
        }
    }
}