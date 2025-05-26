package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.network.request.LeaveRoomRequest
import com.ikseong.ucompass.data.repository.UCompassRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaveRoomUseCase @Inject constructor(
    private val repository: UCompassRepository,
) {
    suspend operator fun invoke(
        deviceId: String,
        roomId: Long,
    ) = repository.leaveRoom(request = LeaveRoomRequest(
        deviceId = deviceId,
        roomId = roomId.toInt(),
    ))
}