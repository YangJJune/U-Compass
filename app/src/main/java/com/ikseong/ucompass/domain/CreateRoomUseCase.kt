package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.network.request.CreateRoomRequest
import com.ikseong.ucompass.data.repository.UCompassRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateRoomUseCase @Inject constructor(
    private val repository: UCompassRepository,
) {
    suspend operator fun invoke(request: CreateRoomRequest) = repository.createRoom(request = request)
}