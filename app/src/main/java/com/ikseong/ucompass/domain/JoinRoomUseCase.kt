package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.network.request.JoinRoomRequest
import com.ikseong.ucompass.data.repository.UCompassRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinRoomUseCase @Inject constructor(
    private val repository: UCompassRepository,
) {
    suspend operator fun invoke(request: JoinRoomRequest) = repository.joinRoom(request = request)
}